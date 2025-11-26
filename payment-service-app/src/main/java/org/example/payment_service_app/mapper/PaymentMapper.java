package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public PaymentDto convertToDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        final PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setValue(payment.getValue());
        dto.setName(payment.getName());
        return dto;
    }

    public List<PaymentDto> convertToDtoList(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return List.of();
        }

        return payments.stream()
                .map(this::convertToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
