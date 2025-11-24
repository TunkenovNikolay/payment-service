package org.example.payment_service_app.service;

import org.example.payment_service_app.EntityExceptions.PaymentNotFoundException;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    public Payment getPaymentById(long id) {
        return paymentRepository.getPaymentById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }
}
