package org.example.xpayment_adapter_app.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@ToString
public class CreateChargeRequestDto {
    private BigDecimal amount;
    private String currency;
    private String customer;
    private UUID order;
    private String receiptEmail;
    private Map<String, Object> metadata = new HashMap<>();
}
