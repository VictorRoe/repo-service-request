package co.com.pragma.api;

import co.com.pragma.api.dto.LoanDetailDTO;
import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.dto.PaginatedResponseDTO;
import co.com.pragma.api.mapper.LoanDTOMapper;
import co.com.pragma.model.loan.Loan;
import co.com.pragma.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanUseCase useCase;
    private final LoanDTOMapper loanDTOMapper;

    @PreAuthorize("hasAuthority('USER')")
    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        log.info("[register] Recibiendo solicitud de registro de préstamo");

        Mono<Loan> loanRequestMono = serverRequest.bodyToMono(LoanRequestDTO.class)
                .doOnNext(dto -> log.debug("[register] DTO recibido: {}", dto))
                .map(loanDTOMapper::toDomain);

        Mono<String> authenticatedUserEmailMono = ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .doOnNext(email -> log.debug("[register] Usuario autenticado extraído del token: {}", email));

        return Mono.zip(loanRequestMono, authenticatedUserEmailMono)
                .flatMap(tuple -> {
                    Loan loanRequest = tuple.getT1();
                    String authenticatedUserEmail = tuple.getT2();

                    return useCase.register(loanRequest, authenticatedUserEmail);
                })
                .doOnNext(loan -> log.info("[register] Préstamo registrado correctamente para el usuario"))
                .then(ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(Map.of("message", "Loan registered successfully")))
                .onErrorResume(AccessDeniedException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(Map.of("error", e.getMessage()))
                );
    }

    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> getRequestsForReview(ServerRequest serverRequest) {
        log.info("[getRequestsForReview] Petición recibida para listar solicitudes.");

        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);

        return useCase.getRequestsForReview(page, size)
                .flatMap(paginatedResponse -> {
                    List<LoanDetailDTO> dtoList = paginatedResponse.items().stream()
                            .map(loanDTOMapper::toDetailDTO)
                            .toList();

                    PaginatedResponseDTO<LoanDetailDTO> responseDTO = new PaginatedResponseDTO<>(
                            dtoList,
                            paginatedResponse.currentPage(),
                            paginatedResponse.totalItems(),
                            paginatedResponse.totalPages()
                    );

                    return ServerResponse.ok().bodyValue(responseDTO);
                })
                .doOnError(ex -> log.error("[getRequestsForReview] Error al procesar la solicitud.", ex))
                .onErrorResume(ex -> Mono.just("Error al procesar la solicitud: " + ex.getMessage())
                        .flatMap(errorMsg -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(Map.of("error", errorMsg)))
                );
    }
}