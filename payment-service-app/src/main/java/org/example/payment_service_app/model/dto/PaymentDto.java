package org.example.payment_service_app.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentDto {
    UUID guid;
    BigDecimal amount;
    String currency;
}
