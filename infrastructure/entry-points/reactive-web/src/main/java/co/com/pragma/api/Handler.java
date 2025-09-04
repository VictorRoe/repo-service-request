package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.mapper.LoanDTOMapper;
import co.com.pragma.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

private  final LoanUseCase useCase;
private final LoanDTOMapper loanDTOMapper;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        log.info("[register] Recibiendo solicitud de registro");
        return serverRequest.bodyToMono(LoanRequestDTO.class)
                .doOnNext(dto -> log.debug("[register] Solicitud recibida: {}", dto))
                .map(loanDTOMapper::toDomain)
                .flatMap(useCase::register)
                .doOnNext(loan -> log.info("[register] Registro procesado correctamente"))
                .then(ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(Map.of("message", "Loan registered successfully")));

    }
}
