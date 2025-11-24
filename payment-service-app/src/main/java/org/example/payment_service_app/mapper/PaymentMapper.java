package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public static PaymentDto convertToDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setValue(payment.getValue());
        dto.setName(payment.getName());
        return dto;
    }

    public static List<PaymentDto> convertToDtoList(Map<Long, Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return List.of();
        }

        return payments.values().stream()
                .map(PaymentMapper::convertToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
