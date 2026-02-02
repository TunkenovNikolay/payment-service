package org.example.xpayment_adapter_app.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public MessageHandlerImpl(AsyncSender<XPaymentAdapterResponseMessage> sender) {
        this.sender = sender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        scheduler.schedule(() -> {
            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}