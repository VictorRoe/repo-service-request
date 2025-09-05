package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class LoanUseCase implements LoanUseCaseImp {

    private static final Logger log = Logger.getLogger(LoanUseCase.class.getName());
    private final LoanRepository repository;

    @Override
    public Mono<Loan> register(Loan loanRequest) {

        log.info("[LoanUseCase] Ejecutando caso de uso register para email");
        if (loanRequest.getLoanType() == null || loanRequest.getLoanType().getId() == null) {
            return Mono.error(new IllegalArgumentException("Loan type is required"));
        }

        loanRequest.setCreatedAt(LocalDateTime.now());
        loanRequest.setStatus(Status.builder()
                .id(1L)
                .name("PENDING")
                .description("Loan request is pending review")
                .build()
        );

        return repository.existsByEmail(loanRequest.getEmail())
                .flatMap(exist -> {
                    if (Boolean.TRUE.equals(exist)) {
                        return Mono.error(new IllegalArgumentException("There is already a request for this email address."));

                    }
                    return repository.saveLoanRequest(loanRequest);
                });
    }
}
