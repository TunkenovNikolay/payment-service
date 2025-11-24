package org.example.payment_service_app.adapter;

import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentAdapter {
    private final PaymentService paymentService;

    @Autowired
    public PaymentAdapter(final PaymentService paymentService) {
        this.paymentService = paymentService;

    }

    public PaymentDto getPaymentById(Long id) {
        return PaymentMapper.convertToDto(paymentService.getPaymentById(id));
    }

    public List<PaymentDto> getAllPayments() {
        return PaymentMapper.convertToDtoList(paymentService.getAllPayments());
    }
}
