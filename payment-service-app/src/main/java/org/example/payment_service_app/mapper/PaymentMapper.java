package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(PaymentMapperDecorator.class)
public interface PaymentMapper {
    String CURRENCY_USD = "USD";

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "note", defaultValue = "some note")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "currency", constant = CURRENCY_USD)
    Payment toEntity(PaymentDto dto);

    @Mapping(target = "currency", source = "filterDto.currency")
    @Mapping(target = "status", source = "dto.status")
    Payment toEntity(PaymentDto dto, PaymentFilterDto filterDto);

    List<PaymentDto> toDto(List<Payment> payments);

    List<Payment> toEntity(List<PaymentDto> dto);

}
