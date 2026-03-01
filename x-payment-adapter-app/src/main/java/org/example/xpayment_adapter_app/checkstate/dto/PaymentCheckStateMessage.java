package org.example.xpayment_adapter_app.checkstate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor // ← Обязательно для Jackson!
@AllArgsConstructor
public class PaymentCheckStateMessage {

    private UUID chargeGuid;
    private UUID paymentGuid;
    private BigDecimal amount;
    private String currency;

}
