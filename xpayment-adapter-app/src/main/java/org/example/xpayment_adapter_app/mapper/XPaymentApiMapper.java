package org.example.xpayment_adapter_app.mapper;

import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.example.xpayment_adapter_app.dto.ChargeResponseDto;
import org.example.xpayment_adapter_app.dto.CreateChargeRequestDto;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterRequestMessage;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterResponseMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface XPaymentApiMapper {

    CreateChargeRequest requestDtoToRequest(CreateChargeRequestDto request);

    ChargeResponseDto responseToResponseDto(ChargeResponse response);

    @Mapping(target = "paymentGuid", source = "order")
    @Mapping(target = "transactionRefId", source = "id")
    @Mapping(target = "occurredAt", expression = "java(java.time.OffsetDateTime.now())")
    XPaymentAdapterResponseMessage responseDtoToKafkaMessage(ChargeResponseDto response);

    @Mapping(target = "order", source = "paymentGuid")
    CreateChargeRequestDto kafkaMessageToRequestDto(XPaymentAdapterRequestMessage request);
}
