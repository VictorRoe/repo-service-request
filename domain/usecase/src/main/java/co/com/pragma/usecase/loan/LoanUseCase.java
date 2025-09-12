package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class LoanUseCase implements LoanUseCaseImp {

    private static final Logger log = Logger.getLogger(LoanUseCase.class.getName());
    private final LoanRepository repository;

    @Override
    public Mono<Loan> register(Loan loanRequest, String authenticatedUserEmail) {

        // 1. Lógica de Seguridad (la que implementamos juntos)
        log.info("[LoanUseCase] Ejecutando caso de uso 'register' para el usuario: " + authenticatedUserEmail);
        if (!authenticatedUserEmail.equals(loanRequest.getEmail())) {
            log.warning("Intento de acceso denegado: El usuario '" + authenticatedUserEmail
                    + "' intentó crear una solicitud para '" + loanRequest.getEmail() + "'.");
            return Mono.error(new AccessDeniedException("Acceso denegado: solo puedes crear solicitudes para ti mismo."));
        }

        // 2. Lógica de Negocio (la nueva validación de la rama 'main')
        return repository.existsByEmail(loanRequest.getEmail())
                .flatMap(exist -> {
                    if (Boolean.TRUE.equals(exist)) {
                        return Mono.error(new IllegalArgumentException("Ya existe una solicitud para esta dirección de correo."));
                    }

                    // 3. Si ambas validaciones pasan, se ejecuta la lógica original
                    if (loanRequest.getLoanType() == null || loanRequest.getLoanType().getId() == null) {
                        return Mono.error(new IllegalArgumentException("El tipo de préstamo es requerido."));
                    }

                    loanRequest.setCreatedAt(LocalDateTime.now());
                    loanRequest.setStatus(Status.builder()
                            .id(1L)
                            .name("PENDING")
                            .description("La solicitud de préstamo está pendiente de revisión.")
                            .build()
                    );

                    return repository.saveLoanRequest(loanRequest)
                            .doOnSuccess(saved -> log.info("Préstamo guardado exitosamente para el usuario " + authenticatedUserEmail))
                            .doOnError(error -> log.severe("Error al guardar el préstamo: " + error.getMessage()));
                });
    }

}
