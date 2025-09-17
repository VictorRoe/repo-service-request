package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.UserDetail;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<UserDetail> findByEmail(String email);
}
