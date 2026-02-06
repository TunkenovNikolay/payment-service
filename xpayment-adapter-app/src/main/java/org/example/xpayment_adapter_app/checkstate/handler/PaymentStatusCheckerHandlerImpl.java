package org.example.xpayment_adapter_app.checkstate.handler;

import lombok.RequiredArgsConstructor;
import org.example.xpayment_adapter_app.api.XPaymentProviderGateway;
import org.example.xpayment_adapter_app.async.AsyncSender;
import org.example.xpayment_adapter_app.dto.ChargeResponseDto;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterResponseMessage;
import org.example.xpayment_adapter_app.mapper.XPaymentApiMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.example.xpayment_adapter_app.async.XPaymentAdapterStatus.CANCELED;
import static org.example.xpayment_adapter_app.async.XPaymentAdapterStatus.SUCCEEDED;

@Service
@RequiredArgsConstructor
public class PaymentStatusCheckerHandlerImpl implements PaymentStatusCheckHandler {

    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGateway gateway;
    private final XPaymentApiMapper mapper;

    @Override
    public boolean handle(UUID id) {
        final ChargeResponseDto dto = gateway.retrieveCharge(id);

        if (dto != null && (dto.getStatus().equals(SUCCEEDED.name()) || dto.getStatus().equals(CANCELED.name()))) {
            asyncSender.send(mapper.responseDtoToKafkaMessage(dto));
            return true;
        } else {
            return false;
        }
    }
}
