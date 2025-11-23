package org.example.payment_service_app.repository;

import org.example.payment_service_app.model.entity.Payment;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
public class PaymentRepository {

    protected Map<Long, Payment> payments = new HashMap<>();

    {
        payments.put(1L, new Payment(1L, 22.22, "Pay for Coca-Cola", LocalDate.of(2009, 3, 23)));
        payments.put(2L, new Payment(2L, 10.5, "Pay for Bread", LocalDate.of(2009, 5, 11)));
        payments.put(3L, new Payment(3L, 22.22, "Pay for Chips", LocalDate.of(2009, 9, 15)));
    }

    public Map<Long, Payment> getAllPayments() {
        return payments;
    }

    public Payment getPaymentById(long id) {
        return payments.get(id);
    }
}
