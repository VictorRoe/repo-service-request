package co.com.pragma.r2dbc;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanType;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import co.com.pragma.r2dbc.entity.LoanEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

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
    private final DatabaseClient databaseClient;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, LoanEntityMapper entityMapper, TransactionalOperator transactionalOperator, DatabaseClient databaseClient) {
        super(repository, mapper, d -> mapper.map(d, Loan.class));
        this.entityMapper = entityMapper;
        this.transactionalOperator =  transactionalOperator;


        this.databaseClient = databaseClient;
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
    public Mono<Boolean> hasActiveRequest(String email) {
        return repository.hasActiveRequestByEmail(email);
    }

    @Override
    public Flux<Loan> findAllByStatusIn(List<String> statuses, int page, int size) {
        String query = "SELECT " +
                "l.id as loan_id, l.email, l.amount, l.term_months, l.created_at, " +
                "lt.id as type_id, lt.name as type_name, lt.interest_rate, " +
                "s.id as status_id, s.name as status_name, s.description as status_description " +
                "FROM loans l " +
                "INNER JOIN loan_type lt ON l.loan_type_id = lt.id " +
                "INNER JOIN status s ON l.status_id = s.id " +
                "WHERE s.name IN (:statuses) " +
                "ORDER BY l.created_at DESC LIMIT :size OFFSET :offset";

        return databaseClient.sql(query)
                .bind("statuses", statuses)
                .bind("size", size)
                .bind("offset", page * size)
                .map((row, metadata) -> {
                    LoanType loanType = LoanType.builder()
                            .id(row.get("type_id", Long.class))
                            .name(row.get("type_name", String.class))
                            .interestRate(row.get("interest_rate", Double.class))
                            .build();

                    Status status = Status.builder()
                            .id(row.get("status_id", Long.class))
                            .name(row.get("status_name", String.class))
                            .description(row.get("status_description", String.class))
                            .build();

                    return Loan.builder()
                            .id(row.get("loan_id", Long.class))
                            .email(row.get("email", String.class))
                            .amount(row.get("amount", Long.class))
                            .termMonths(row.get("term_months", Integer.class))
                            .createdAt(row.get("created_at", LocalDateTime.class))
                            .loanType(loanType)
                            .status(status)
                            .build();
                }).all();
    }

    @Override
    public Mono<Long> countByStatusIn(List<String> statuses) {
        String query = "SELECT COUNT(l.id) FROM loans l INNER JOIN status s ON l.status_id = s.id WHERE s.name IN (:statuses)";
        return databaseClient.sql(query)
                .bind("statuses", statuses)
                .map((row, metadata) -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> calculateApprovedMonthlyDebt(String email) {
        String query = "SELECT COALESCE(SUM(l.amount / l.term_months), 0) FROM loans l JOIN status s ON l.status_id = s.id WHERE l.email = :email AND s.name = 'APPROVED'";

        return databaseClient.sql(query)
                .bind("email", email)
                .map((row, metadata) -> row.get(0, Long.class))
                .one();
    }
}
