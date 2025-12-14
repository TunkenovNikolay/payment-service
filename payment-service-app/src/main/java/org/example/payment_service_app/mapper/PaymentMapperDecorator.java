package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;

public abstract class PaymentMapperDecorator implements PaymentMapper {

    private PaymentMapper delegate;

    public void setDelegate(PaymentMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public PaymentDto toDto(Payment payment) {
        final PaymentDto dto = delegate.toDto(payment);
        dto.setNote(getNote());
        return dto;
    }

    private String getNote() {
        return "new note";
    }

}
