package co.com.pragma.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name= "Loan", description = "Creacion de prestamo")
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {"application/json"},
                    method = {org.springframework.web.bind.annotation.RequestMethod.POST},
                    beanClass = Handler.class,
                    beanMethod = "register",
                    operation = @Operation(
                            summary = "Crear una solicitud de préstamo",
                            description = "Crea un nuevo préstamo en el sistema"
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::register)
                .andRoute(GET("/api/v1/solicitud"), handler::getRequestsForReview)
                .andRoute(PUT("/api/v1/solicitud/{id}"), handler::approveOrRejectLoan);
    }
}
