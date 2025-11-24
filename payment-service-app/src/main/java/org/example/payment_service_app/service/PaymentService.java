package org.example.payment_service_app.service;

import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentService(final PaymentRepository paymentRepository, final PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }


    public Optional<PaymentDto> getPaymentDtoById(long id) {
        return paymentRepository.getPaymentById(id)
                .map(paymentMapper::convertToDto);
    }

    public List<PaymentDto> getAllPaymentsDto() {
        return paymentMapper.convertToDtoList(paymentRepository.getAllPayments());
    }
}
