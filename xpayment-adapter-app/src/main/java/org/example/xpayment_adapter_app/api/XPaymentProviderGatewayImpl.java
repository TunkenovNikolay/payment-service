package org.example.xpayment_adapter_app.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.example.xpayment_adapter_app.dto.ChargeResponseDto;
import org.example.xpayment_adapter_app.dto.CreateChargeRequestDto;
import org.example.xpayment_adapter_app.mapper.XPaymentApiMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    private final XPaymentApiMapper xPaymentApiMapper;

    @Override
    public ChargeResponseDto createCharge(CreateChargeRequestDto dto) throws RestClientException {
        try {
            final CreateChargeRequest request = xPaymentApiMapper.requestDtoToRequest(dto);
            final ChargeResponse response = defaultApi.createCharge(request);
            return xPaymentApiMapper.responseToResponseDto(response);
        } catch (ApiException e) {
            log.error(e.getMessage());
            throw new RestClientException(e.getMessage());
        }
    }

}
