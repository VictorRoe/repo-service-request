package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanDetails;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import co.com.pragma.model.notification.gateways.NotificationRepository;
import co.com.pragma.model.user.UserDetail;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class LoanUseCase implements LoanUseCaseImp {

    private static final Logger log = Logger.getLogger(LoanUseCase.class.getName());
    private final LoanRepository repository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private static final Map<String, Long> DECISION_STATUS_MAP = Map.of(
            "APPROVED", 2L,
            "REJECTED", 3L
    );

    @Override
    public Mono<Loan> register(Loan loanRequest, String authenticatedUserEmail) {
        if (!authenticatedUserEmail.equals(loanRequest.getEmail())) {
            return Mono.error(new AccessDeniedException("Acceso denegado: solo puedes crear solicitudes para ti mismo."));
        }

        return repository.hasActiveRequest(loanRequest.getEmail())
                .flatMap(hasActive -> {
                    // 3. Verificamos si ya tiene una solicitud activa
                    if (Boolean.TRUE.equals(hasActive)) {
                        return Mono.error(new IllegalArgumentException("Ya existe una solicitud activa (pendiente o en revisión) para este correo."));
                    }

                    // --- FIN DE LOS CAMBIOS ---
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

    @Override
    public Mono<PaginatedResponse<LoanDetails>> getRequestsForReview(int page, int size) {

        // 1. Define los estados de las solicitudes que necesita el Asesor.
        List<String> statusesToReview = List.of("PENDING", "REJECTED", "MANUAL_REVIEW");

        // 2. Llama al repositorio para obtener el conteo total y la página de préstamos actual.
        //    Usamos Mono.zip para hacer ambas llamadas en paralelo y ser más eficientes.
        Mono<Long> totalItemsMono = repository.countByStatusIn(statusesToReview);
        Flux<Loan> loansPageFlux = repository.findAllByStatusIn(statusesToReview, page, size);

        // 3. Para cada préstamo, lo "enriquecemos" con datos adicionales.
        Flux<LoanDetails> detailedLoansFlux = loansPageFlux.flatMap(this::buildLoanDetail);

        // 4. Juntamos la lista de préstamos enriquecidos y el conteo total para armar la respuesta final.
        return Mono.zip(detailedLoansFlux.collectList(), totalItemsMono)
                .map(tuple -> {
                    List<LoanDetails> loanDetails = tuple.getT1();
                    Long totalItems = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalItems / size);
                    return new PaginatedResponse<>(loanDetails, page, totalItems, totalPages);
                });
    }

    @Override
    public Mono<Loan> approveOrRejectLoan(Long loanId, String decision) {
        Long newStatusId = DECISION_STATUS_MAP.get(decision.toUpperCase());
        if (newStatusId == null) {
            return Mono.error(new IllegalArgumentException("La decisión debe ser 'APPROVED' o 'REJECTED'."));
        }
        return repository.updateStatus(loanId, newStatusId)
                .flatMap(updatedLoan ->
                        notificationRepository.sendLoanDecisionNotification(updatedLoan)
                                .then(Mono.just(updatedLoan))
                );
    }

    /**
     * Método privado que toma un objeto Loan y lo enriquece con datos del usuario y su deuda.
     *
     * @param loan El préstamo a enriquecer.
     * @return Un Mono que emite el LoanDetail completo.
     */
    private Mono<LoanDetails> buildLoanDetail(Loan loan) {
        // Hacemos dos llamadas en paralelo: una para los detalles del usuario y otra para su deuda.
        Mono<UserDetail> userDetailMono = userRepository.findByEmail(loan.getEmail())
                .onErrorReturn(new UserDetail("Usuario no encontrado", 0L)); // Si falla la llamada al otro servicio, devolvemos datos por defecto.

        Mono<Long> monthlyDebtMono = repository.calculateApprovedMonthlyDebt(loan.getEmail());

        // Combinamos los resultados de las dos llamadas.
        return Mono.zip(userDetailMono, monthlyDebtMono)
                .map(tuple -> {
                    UserDetail userDetail = tuple.getT1();
                    Long monthlyDebt = tuple.getT2();

                    // Construimos y devolvemos el objeto final y enriquecido.
                    return LoanDetails.builder()
                            .loan(loan)
                            .fullName(userDetail.fullName())
                            .userBaseSalary(userDetail.baseSalary())
                            .totalMonthlyDebt(monthlyDebt)
                            .build();
                });
    }

}
