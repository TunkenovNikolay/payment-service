package org.example.payment_service_app.service;

import org.example.payment_service_app.exception.custom.PaymentNotFoundException;
import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.example.payment_service_app.repository.PaymentRepository;
import org.example.payment_service_app.repository.specification.PaymentFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.example.payment_service_app.util.TimeUtil.getNow;

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

    public PaymentDto createPayment(PaymentDto paymentDto) {
        final Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setCreatedAt(getNow());
        payment.setUpdatedAt(getNow());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public PaymentDto updatePayment(UUID id, PaymentDto paymentDto) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException(id);
        }
        final Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setGuid(id);
        payment.setUpdatedAt(getNow());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public void deletePayment(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException(id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus paymentStatus) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setStatus(paymentStatus);
        payment.setUpdatedAt(getNow());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable) {
        final Specification<Payment> spec = PaymentFilterFactory.filter(filter);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

}
