package org.example.payment_service_app.service;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentDto getPaymentByUuid(UUID id);

    List<PaymentDto> getAllPayments();

    Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable);

}
