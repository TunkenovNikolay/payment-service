package org.example.payment_service_app.repository;

import org.example.payment_service_app.model.entity.Payment;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class PaymentRepository {

    protected Map<Long, Payment> payments = new HashMap<>();

    {
        payments.put(1L, new Payment(1L, 22.22, "Pay for Coca-Cola", Instant.parse("2009-03-24T10:00:00Z")));
        payments.put(2L, new Payment(2L, 10.5, "Pay for Bread", Instant.parse("2009-03-11T10:00:00Z")));
        payments.put(3L, new Payment(3L, 22.22, "Pay for Chips", Instant.parse("2009-09-15T10:00:00Z")));
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public Optional<Payment> getPaymentById(long id) {
        return Optional.ofNullable(payments.get(id));
    }
}
