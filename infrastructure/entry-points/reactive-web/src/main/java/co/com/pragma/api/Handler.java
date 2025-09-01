package co.com.pragma.api;

import co.com.pragma.api.dto.CreateLoanRequestDTO;
import co.com.pragma.api.dto.LoanRequestResponseDTO;
import co.com.pragma.api.mapper.LoanDTOMapper;
import co.com.pragma.model.loan.LoanType;
import co.com.pragma.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

private  final LoanUseCase useCase;
private final LoanDTOMapper loanDTOMapper;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        log.info("[handler] POST /api/v1/solicitud -> recibido");
        return serverRequest.bodyToMono(CreateLoanRequestDTO.class)
                .map(loanDTOMapper::toModel)
                .flatMap(useCase::register)
                .map(loanDTOMapper::toResponse)
                .flatMap((LoanRequestResponseDTO resp) -> ServerResponse
                        .created(URI.create("/api/v1/solicitud/" + resp.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp)
                )
                .onErrorResume(e -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", e.getMessage()))
                );
    }
}
