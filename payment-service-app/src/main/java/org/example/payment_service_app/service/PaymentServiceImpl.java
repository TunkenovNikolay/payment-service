package org.example.payment_service_app.service;

import org.example.payment_service_app.exception.custom.PaymentNotFoundException;
import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.repository.PaymentRepository;
import org.example.payment_service_app.repository.specification.PaymentFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(final PaymentRepository paymentRepository, final PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentDto getPaymentByUuid(UUID id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toDto)
            .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public List<PaymentDto> getAllPayments() {
        return paymentMapper.toDto(paymentRepository.findAll());
    }

    public Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable) {
        final Specification<Payment> spec = PaymentFilterFactory.filter(filter);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

}
