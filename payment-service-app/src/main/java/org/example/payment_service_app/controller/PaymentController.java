package org.example.payment_service_app.controller;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.service.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentServiceImpl paymentServiceImpl;

    @Autowired
    public PaymentController(PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;

    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentByUuid(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentServiceImpl.getPaymentByUuid(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments() {
        return ResponseEntity.ok(paymentServiceImpl.getAllPayments());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PaymentDto>> searchPayments(
        @ModelAttribute PaymentFilterDto filter,
        @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok().body(paymentServiceImpl.search(filter, pageable));
    }
}
