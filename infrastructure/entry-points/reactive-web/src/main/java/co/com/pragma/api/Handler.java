package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.mapper.LoanDTOMapper;
import co.com.pragma.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

private  final LoanUseCase useCase;
private final LoanDTOMapper loanDTOMapper;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanRequestDTO.class)
                .map(loanDTOMapper::toDomain)
                .flatMap(useCase::register)
                .map(loanDTOMapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}
