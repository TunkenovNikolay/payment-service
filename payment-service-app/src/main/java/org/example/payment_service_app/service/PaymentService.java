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

    @Autowired
    public PaymentService(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    public Optional<PaymentDto> getPaymentDtoById(long id) {
        return paymentRepository.getPaymentById(id)
                .map(PaymentMapper::convertToDto);
    }

    public List<PaymentDto> getAllPaymentsDto() {
        return PaymentMapper.convertToDtoList(paymentRepository.getAllPayments());
    }
}
