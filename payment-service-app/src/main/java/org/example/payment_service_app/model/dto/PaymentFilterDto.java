package org.example.payment_service_app.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.payment_service_app.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class PaymentFilterDto {

    private PaymentStatus status;
    private String currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Instant createdAfter;
    private Instant createdBefore;

}
