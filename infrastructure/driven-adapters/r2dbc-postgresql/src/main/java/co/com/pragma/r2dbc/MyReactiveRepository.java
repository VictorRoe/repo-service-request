package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.LoanEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<LoanEntity, Long>, ReactiveQueryByExampleExecutor<LoanEntity> {

}
