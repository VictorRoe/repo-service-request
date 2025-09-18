package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.LoanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MyReactiveRepository extends ReactiveCrudRepository<LoanEntity, Long>, ReactiveQueryByExampleExecutor<LoanEntity> {

    @Query("SELECT EXISTS(SELECT 1 FROM loans l INNER JOIN status s ON l.status_id = s.id WHERE l.email = :email AND s.name IN ('PENDING', 'MANUAL_REVIEW'))")
    Mono<Boolean> hasActiveRequestByEmail(@Param("email") String email);

    Mono<LoanEntity> findById(Long id);

}
