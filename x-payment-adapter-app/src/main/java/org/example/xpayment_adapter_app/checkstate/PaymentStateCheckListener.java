package org.example.xpayment_adapter_app.checkstate;

import lombok.RequiredArgsConstructor;
import org.example.xpayment_adapter_app.checkstate.dto.PaymentCheckStateMessage;
import org.example.xpayment_adapter_app.checkstate.handler.PaymentStatusCheckHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStateCheckListener {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.delayed-exchange-name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue-name}")
    private String routingKey;

    @Value("${spring.rabbitmq.dlx-exchange-name}")
    private String dlxExchangeName;

    @Value("${spring.rabbitmq.dlx-routing-key}")
    private String dlxRoutingKey;

    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @Value("${spring.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${spring.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @RabbitListener(queues = "${spring.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        final MessageProperties props = raw.getMessageProperties();
        final int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);
        final boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
            final PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(
                message.getChargeGuid(),
                message.getPaymentGuid(),
                message.getAmount(),
                message.getCurrency());
            rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                newMessage,
                m -> {
                    m.getMessageProperties().setHeader("x-delay", intervalMs);
                    m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                    return m;
                }
            );
        } else {
// Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(
                dlxExchangeName,
                dlxRoutingKey,
                message,
                m -> {
                    m.getMessageProperties().setHeader("x-retry-count", retryCount);
                    m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                    m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                    return m;
                }
            );
        }
    }
}
