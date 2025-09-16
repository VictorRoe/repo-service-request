package co.com.pragma.consumer;

import co.com.pragma.model.user.UserDetail;
import co.com.pragma.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceAdapter implements UserRepository{

    private final WebClient client;

    @Override
    @CircuitBreaker(name = "auth-service")
    public Mono<UserDetail> findByEmail(String email) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String token = context.getAuthentication().getCredentials().toString();
                    return client.get()
                            .uri("/api/v1/users/details/{email}", email)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .retrieve()
                            .bodyToMono(UserDetail.class);
                })
                .onErrorResume(ex -> {
                    System.err.println("Error llamando a auth-service para el email " + email + ": " + ex.getMessage());
                    return Mono.just(new UserDetail("Usuario no disponible", 0L));
                });

    }
}
