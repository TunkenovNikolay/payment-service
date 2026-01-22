package org.example.payment_service_app.controller;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.dto.PaymentStatusUpdateDto;
import org.example.payment_service_app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDto> getPaymentByUuid(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentByUuid(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'READER')")
    public ResponseEntity<List<PaymentDto>> getPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> update(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, paymentDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> update(
        @PathVariable UUID id,
        @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto) {
        return ResponseEntity.ok(paymentService.updateStatus(id, paymentStatusUpdateDto.getStatus()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'READER', 'ADMIN')")
    public ResponseEntity<Page<PaymentDto>> search(
        @ModelAttribute PaymentFilterDto filter,
        @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok().body(paymentService.search(filter, pageable));
    }
}
