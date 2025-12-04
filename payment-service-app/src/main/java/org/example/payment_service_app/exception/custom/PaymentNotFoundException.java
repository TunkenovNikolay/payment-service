package org.example.payment_service_app.exception.custom;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentNotFoundException extends RuntimeException {
    private final UUID paymentId;

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment not found with id: " + paymentId);
        this.paymentId = paymentId;
    }
}
