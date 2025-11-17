package org.example.payment_service_app.model.dto;

import lombok.Data;

@Data
public class PaymentDto {
    Long id;
    Double value;
    String name;
}
