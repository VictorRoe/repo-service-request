package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface LoanUseCaseImp {

    Mono<Loan> register(Loan loanRequest);
}
