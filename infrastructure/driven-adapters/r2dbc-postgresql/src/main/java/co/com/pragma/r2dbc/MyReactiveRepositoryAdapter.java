package co.com.pragma.r2dbc;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.gateways.LoanRepository;
import co.com.pragma.r2dbc.entity.LoanEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Loan,
        LoanEntity,
        Long,
        MyReactiveRepository
> implements LoanRepository {

    private final LoanEntityMapper entityMapper;
    private final TransactionalOperator transactionalOperator;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, LoanEntityMapper entityMapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, Loan.class));
        this.entityMapper = entityMapper;
        this.transactionalOperator =  transactionalOperator;


    }

    @Override
    public Mono<Loan> saveLoanRequest(Loan loanRequest) {
        log.info("Guardando Loan en la base de datos");
        LoanEntity entity = entityMapper.toEntity(loanRequest);
        return repository.save(entity)
                .map(entityMapper::toDomain)
                .as(transactionalOperator::transactional)
                .doOnSuccess(saved -> log.debug("Entidad guardada: {}", saved))
                .doOnError(error -> log.error("Error en la capa de persistencia", error));
    }


    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
