package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public Optional<PaymentDto> convertToDto(Payment payment) {
        return Optional.ofNullable(payment)
                .map(p -> {
                    PaymentDto dto = new PaymentDto();
                    dto.setId(p.getId());
                    dto.setValue(p.getValue());
                    dto.setName(p.getName());
                    return dto;
                });
    }

    public List<PaymentDto> convertToDtoList(Map<Long, Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return List.of();
        }

        return payments.values().stream()
                .map(this::convertToDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
