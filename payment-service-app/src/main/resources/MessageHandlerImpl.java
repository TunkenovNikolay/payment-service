package org.example.payment_service_app.async;

import lombok.extern.slf4j.Slf4j;
import org.example.payment_service_app.exception.ErrorMessage;
import org.example.payment_service_app.exception.ServiceException;
import org.example.payment_service_app.mapper.XPaymentAdapterMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentService paymentService;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;

    @Autowired
    MessageHandlerImpl(PaymentService paymentService, XPaymentAdapterMapper xPaymentAdapterMapper) {
        this.paymentService = paymentService;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
    }

    @Override
    @Retryable(
        value = {ServiceException.class, Exception.class},
        maxRetries = 3,
        delay = 1000,
        multiplier = 2
    )
    public void handle(XPaymentAdapterResponseMessage message) {
        if (message == null || message.getPaymentGuid() == null) {
            log.warn("Received invalid or null message");
            return;
        }

        try {
            log.info(
                "Processing XPaymentAdapterResponseMessage, messageId = {}, paymentGuid = {}, dateTimeMessage = {}",
                message.getMessageId(), message.getPaymentGuid(), message.getOccurredAt());

            final PaymentDto paymentDto = xPaymentAdapterMapper.toPaymentDto(message);
            paymentService.updatePayment(message.getPaymentGuid(), paymentDto);

            log.info("Successfully processed payment update for guid: {}",
                message.getPaymentGuid());

        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", message.getMessageId(), e);
            throw new ServiceException(ErrorMessage.UNEXPECTED_ERROR_PROCESSING_MESSAGE, message.getMessageId());
        }
    }

}
