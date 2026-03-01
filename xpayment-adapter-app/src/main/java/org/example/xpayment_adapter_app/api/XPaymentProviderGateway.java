package org.example.xpayment_adapter_app.api;

import org.example.xpayment_adapter_app.dto.ChargeResponseDto;
import org.example.xpayment_adapter_app.dto.CreateChargeRequestDto;
import org.springframework.web.client.RestClientException;

public interface XPaymentProviderGateway {

    ChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException;

}
