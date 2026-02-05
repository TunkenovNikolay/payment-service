package org.example.xpayment_adapter_app.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpayment_adapter_app.api.XPaymentProviderGateway;
import org.example.xpayment_adapter_app.dto.ChargeResponseDto;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterRequestMessage;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterResponseMessage;
import org.example.xpayment_adapter_app.mapper.XPaymentApiMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentApiMapper mapper;

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
            message.getPaymentGuid(), message.getAmount(), message.getCurrency());
        try {
            final ChargeResponseDto dto = xPaymentProviderGateway.createCharge(
                mapper.kafkaMessageToRequestDto(message));
            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                dto.getStatus());
            asyncSender.send(mapper.responseDtoToKafkaMessage(dto));
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);
            asyncSender.send(getResponseMessage(message));
        }
    }

    private XPaymentAdapterResponseMessage getResponseMessage(XPaymentAdapterRequestMessage message) {
        final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
        responseMessage.setMessageGuid(UUID.randomUUID());
        responseMessage.setPaymentGuid(message.getPaymentGuid());
        responseMessage.setAmount(message.getAmount());
        responseMessage.setCurrency(message.getCurrency());
        responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
        responseMessage.setOccurredAt(OffsetDateTime.now());
        return responseMessage;
    }
}
