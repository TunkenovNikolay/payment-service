package org.example.payment_service_app.mapper;

import org.example.payment_service_app.async.XPaymentAdapterRequestMessage;
import org.example.payment_service_app.async.XPaymentAdapterResponseMessage;
import org.example.payment_service_app.async.XPaymentAdapterStatus;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface XPaymentAdapterMapper {

    @Mapping(source = "guid", target = "paymentGuid")
    @Mapping(source = "updatedAt", target = "occurredAt")
    XPaymentAdapterRequestMessage toXPaymentAdapterRequestMessage(Payment payment);

    @Mapping(source = "paymentGuid", target = "guid")
    @Mapping(source = "transactionRefId", target = "transactionRefId")
    @Mapping(source = "status", target = "status")
    PaymentDto toPaymentDto(XPaymentAdapterResponseMessage message);

    default PaymentStatus mapStatus(XPaymentAdapterStatus status) {
        return XPaymentAdapterStatus.CANCELED.equals(status)
            ? PaymentStatus.DECLINED
            : PaymentStatus.APPROVED;
    }
}