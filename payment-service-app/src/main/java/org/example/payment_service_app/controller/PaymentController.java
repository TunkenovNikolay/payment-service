package org.example.payment_service_app.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.payment_service_app.config.security.UserContext;
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

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserContext userContext;

    @Autowired
    public PaymentController(PaymentService paymentService, UserContext userContext) {
        this.paymentService = paymentService;
        this.userContext = userContext;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDto> getPaymentByUuid(@PathVariable UUID id) {
        log.info("GET payments/{} - User: {} (sub: {}), Roles: {}",
            id, userContext.getUsername(), userContext.getRoles(), userContext.getUserId());

        final PaymentDto paymentDto = paymentService.getPaymentByUuid(id);

        log.debug("Sending response by User: {} (sub: {}), Roles: {}, PaymentId{}",
            userContext.getUsername(), userContext.getRoles(), userContext.getUserId(), paymentDto.getGuid());

        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'READER')")
    public ResponseEntity<List<PaymentDto>> getPayments() {
        log.info("GET payments - User: {} (sub: {}), Roles: {}",
            userContext.getUsername(), userContext.getRoles(), userContext.getUserId());

        final List<PaymentDto> paymentDtoList = paymentService.getAllPayments();

        log.debug("Sending response by User: {} (sub: {}), Roles: {}, count payments: {}",
            userContext.getUsername(), userContext.getRoles(), userContext.getUserId(), paymentDtoList.size());

        return ResponseEntity.ok(paymentDtoList);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto paymentDto) {
        log.info("POST payments - User: {} (sub: {}), Roles: {}",
            userContext.getUsername(),
            userContext.getUserId(),
            userContext.getRoles());

        final PaymentDto createdPayment = paymentService.createPayment(paymentDto);

        log.debug("PaymentId {} created by User: {} - Status: {}",
            createdPayment.getGuid(),
            userContext.getUsername(),
            createdPayment.getStatus());

        return ResponseEntity.ok(createdPayment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> update(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        log.info("UPDATE payment/{} - User: {} (sub: {}), Roles: {}",
            id,
            userContext.getUsername(),
            userContext.getUserId(),
            userContext.getRoles());

        final PaymentDto updatedPayment = paymentService.updatePayment(id, paymentDto);

        log.debug("Payment {} updated by User: {}",
            updatedPayment.getGuid(),
            userContext.getUsername());

        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE payment/{} - User: {} (sub: {}), Roles: {}",
            id,
            userContext.getUsername(),
            userContext.getUserId(),
            userContext.getRoles());

        paymentService.deletePayment(id);

        log.debug("Payment {} deleted by User: {}",
            id,
            userContext.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> update(
        @PathVariable UUID id,
        @RequestBody PaymentStatusUpdateDto paymentStatusUpdateDto) {
        log.info("Patch payment/{}/status - User: {} (sub: {}), Roles: {},",
            id,
            userContext.getUsername(),
            userContext.getUserId(),
            userContext.getRoles());

        final PaymentDto patchedPaymentDto = paymentService.updateStatus(id, paymentStatusUpdateDto.getStatus());

        log.debug("Payment {} patched by User: {}",
            id,
            userContext.getUsername());

        return ResponseEntity.ok(patchedPaymentDto);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'READER', 'ADMIN')")
    public ResponseEntity<Page<PaymentDto>> search(
        @ModelAttribute PaymentFilterDto filter,
        @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        log.info("Get search - User: {} (sub: {}), Roles: {}, pageable: {}",
            userContext.getUsername(),
            userContext.getUserId(),
            userContext.getRoles(),
            pageable);

        final Page<PaymentDto> paymentDtoPage = paymentService.search(filter, pageable);

        log.debug("Searched by User: {} (sub: {}), Roles: {}, count payments: {}",
            userContext.getUsername(), userContext.getRoles(), userContext.getUserId(), paymentDtoPage.get().count());

        return ResponseEntity.ok().body(paymentDtoPage);
    }
}
