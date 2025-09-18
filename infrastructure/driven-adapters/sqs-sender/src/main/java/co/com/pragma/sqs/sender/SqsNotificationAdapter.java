package co.com.pragma.sqs.sender;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.notification.gateways.NotificationRepository;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SqsNotificationAdapter implements NotificationRepository {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> sendLoanDecisionNotification(Loan loan) {
        log.info("Enviando evento de decisión a SQS para el préstamo ID: {}", loan.getId());
        try {
            String messageBody = objectMapper.writeValueAsString(loan);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(properties.queueUrl())
                    .messageBody(messageBody)
                    .build();

            return Mono.fromFuture(client.sendMessage(request))
                    .doOnSuccess(response -> log.info("Mensaje enviado a SQS con éxito. MessageId: {}", response.messageId()))
                    .doOnError(ex -> log.error("Error al enviar mensaje a SQS.", ex))
                    .then();

        } catch (Exception e) {
            log.error("Error al serializar el objeto Loan a JSON.", e);
            return Mono.error(e);
        }
    }
}
