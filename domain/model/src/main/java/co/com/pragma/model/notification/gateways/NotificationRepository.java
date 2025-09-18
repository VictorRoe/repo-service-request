package co.com.pragma.model.notification.gateways;

import co.com.pragma.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface NotificationRepository {

    /**
     * Envía una notificación sobre la decisión de un préstamo.
     * @param loan El objeto del préstamo con su estado ya actualizado.
     * @return Un Mono<Void> que se completa cuando el mensaje es enviado.
     */
    Mono<Void> sendLoanDecisionNotification(Loan loan);
}
