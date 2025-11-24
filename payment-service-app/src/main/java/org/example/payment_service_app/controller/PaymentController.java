package org.example.payment_service_app.controller;

import org.example.payment_service_app.adapter.PaymentAdapter;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentAdapter paymentAdapter;

    @Autowired
    public PaymentController(PaymentAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;

    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable long id) {
        try {
            PaymentDto payment = paymentAdapter.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments() {
        return ResponseEntity.ok(paymentAdapter.getAllPayments());
    }
}
