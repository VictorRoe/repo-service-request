package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanDetails;
import reactor.core.publisher.Mono;

public interface LoanUseCaseImp {

    Mono<Loan> register(Loan loanRequest, String authenticatedUserEmail);

    /**
     * Obtiene una lista paginada de solicitudes de préstamo para revisión del asesor.
     * @param page Número de página.
     * @param size Tamaño de la página.
     * @return Un Mono que contiene una página de detalles de préstamos.
     */
    Mono<PaginatedResponse<LoanDetails>> getRequestsForReview(int page, int size);

    /**
     * Aprueba o rechaza una solicitud de préstamo.
     * @param loanId El ID del préstamo a modificar.
     * @param decision La decisión del asesor ("APPROVED" o "REJECTED").
     * @return Un Mono que emite el préstamo actualizado.
     */
    Mono<Loan> approveOrRejectLoan(Long loanId, String decision);
}
