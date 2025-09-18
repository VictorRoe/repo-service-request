package co.com.pragma.model.loan.gateways;

import co.com.pragma.model.loan.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanRepository {

    Mono<Loan> saveLoanRequest(Loan loanRequest);
    Mono<Boolean> hasActiveRequest(String email);

    Mono<Loan>findById(Long id);

    /**
     * Busca una lista paginada de préstamos que coincidan con una lista de estados.
     * @param statuses Lista de nombres de estados (ej. "PENDING", "REJECTED").
     * @param page Número de página (empezando en 0).
     * @param size Tamaño de la página.
     * @return Un Flux (flujo) de préstamos que coinciden.
     */
    Flux<Loan> findAllByStatusIn(List<String> statuses, int page, int size);

    /**
     * Cuenta el número total de préstamos que coinciden con una lista de estados.
     * (Necesario para la paginación).
     * @param statuses Lista de nombres de estados.
     * @return Un Mono con el conteo total.
     */
    Mono<Long> countByStatusIn(List<String> statuses);

    /**
     * Calcula la deuda mensual total de todos los préstamos APROBADOS para un email específico.
     * @param email Email del usuario.
     * @return Un Mono con la suma de las cuotas mensuales.
     */
    Mono<Long> calculateApprovedMonthlyDebt(String email);

    /**
     * Actualiza el estado de una solicitud de préstamo existente.
     * @param loanId El ID del préstamo a actualizar.
     * @param statusId El ID del nuevo estado.
     * @return Un Mono que emite el préstamo actualizado.
     */
    Mono<Loan> updateStatus(Long loanId, Long statusId);
}
