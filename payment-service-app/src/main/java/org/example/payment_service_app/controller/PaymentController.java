package org.example.payment_service_app.controller;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;

    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentByUuid(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentByUuid(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/search")
    public Page<Payment> searchPayments(
        @ModelAttribute PaymentFilterDto filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "createdAt") String sortedBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        final Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortedBy).descending()
            : Sort.by(sortedBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }

    @GetMapping("/search2")
    public ResponseEntity<Page<PaymentDto>> searchPayments2(
        @ModelAttribute PaymentFilterDto filter,
        @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok().body(paymentService.searchPaged2(filter, pageable));
    }
}
