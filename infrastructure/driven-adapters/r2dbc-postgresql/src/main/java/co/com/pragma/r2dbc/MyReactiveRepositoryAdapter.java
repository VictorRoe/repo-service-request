package co.com.pragma.r2dbc;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.gateways.LoanRepository;
import co.com.pragma.r2dbc.entity.LoanEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Loan,
        LoanEntity,
        Long,
        MyReactiveRepository
> implements LoanRepository {

    private final TransactionalOperator transactionalOperator;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, Loan.class));
        this.transactionalOperator =  transactionalOperator;


    }

    @Override
    public Mono<Loan> saveLoanRequest(Loan loanRequest) {
        return this.save(loanRequest).as(transactionalOperator::transactional);
    }
}
