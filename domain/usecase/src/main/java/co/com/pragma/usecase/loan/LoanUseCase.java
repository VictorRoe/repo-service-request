package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoanUseCase implements LoanUseCaseImp{

    private final LoanRepository repository;

    @Override
    public Mono<Loan> register(Loan loanRequest) {
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
        return repository.saveLoanRequest(loanRequest);
    }
}
