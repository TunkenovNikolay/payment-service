package org.example.xpayment_adapter_app.checkstate;

import lombok.RequiredArgsConstructor;
import org.example.xpayment_adapter_app.checkstate.dto.PaymentCheckStateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentStateCheckRegistrarImpl implements PaymentStateCheckRegistrar {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.delayed-exchange-name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.queue-name}")
    private String routingKey;

    @Value("${spring.rabbitmq.max-retries:10}")
    private int maxRetries;

    @Value("${spring.rabbitmq.interval-ms:40000}")
    private long intervalMs;

    @Override
    public void register(UUID chargeGuid, UUID paymentGuid, BigDecimal amount, String currency) {
        final PaymentCheckStateMessage message =
            new PaymentCheckStateMessage(chargeGuid, paymentGuid, amount, currency);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, m -> {
                m.getMessageProperties().setHeader("x-delay", intervalMs);
                m.getMessageProperties().setHeader("x-retry-count", 1);
                return m;
            }
        );
    }
}
