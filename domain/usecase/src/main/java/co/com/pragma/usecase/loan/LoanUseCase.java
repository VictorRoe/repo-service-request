package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.RequestStatus;
import co.com.pragma.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase implements LoanUseCaseImp{

    private final LoanRepository repository;

    @Override
    public Mono<Loan> register(Loan loanRequest) {
        loanRequest.setStatus(RequestStatus.PENDIENTE);
        return repository.saveLoanRequest(loanRequest);
    }
}
