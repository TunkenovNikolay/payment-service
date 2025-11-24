package org.example.payment_service_app.exception.custom;

import lombok.Getter;

@Getter
public class PaymentNotFoundException extends RuntimeException {
    private final Long paymentId;

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with id: " + paymentId);
        this.paymentId = paymentId;
    }
}
