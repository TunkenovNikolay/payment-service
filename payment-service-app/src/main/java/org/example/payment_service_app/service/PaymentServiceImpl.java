package org.example.payment_service_app.service;

import lombok.extern.slf4j.Slf4j;
import org.example.payment_service_app.async.AsyncSender;
import org.example.payment_service_app.async.XPaymentAdapterRequestMessage;
import org.example.payment_service_app.exception.ErrorMessage;
import org.example.payment_service_app.exception.ServiceException;
import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.mapper.XPaymentAdapterMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.example.payment_service_app.exception.ErrorMessage.PAYMENT_NOT_EXIST;
import static org.example.payment_service_app.util.TimeUtil.getNow;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

    @Autowired
    public PaymentServiceImpl(final PaymentRepository paymentRepository, final PaymentMapper paymentMapper,
                              final XPaymentAdapterMapper xPaymentAdapterMapper,
                              final AsyncSender<XPaymentAdapterRequestMessage> sender) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
        this.sender = sender;
    }

    public PaymentDto getPaymentByUuid(UUID id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toDto)
            .orElseThrow(() -> new ServiceException(PAYMENT_NOT_EXIST, id));
    }

    public List<PaymentDto> getAllPayments() {
        return paymentMapper.toDto(paymentRepository.findAll());
    }

    public PaymentDto createPayment(PaymentDto paymentDto) {
        final Payment payment = paymentMapper.toEntity(paymentDto);
        final UUID guid = UUID.randomUUID();
        payment.setGuid(guid);
        payment.setStatus(PaymentStatus.RECEIVED);
        payment.setCreatedAt(getNow());
        payment.setUpdatedAt(getNow());

        final PaymentDto response = paymentMapper.toDto(paymentRepository.save(payment));

        //Создаем сущность (XPayment) и отправляем запрос в брокер
        try {
            final XPaymentAdapterRequestMessage xPaymentAdapterRequestMessage =
                xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(payment);
            sender.send(xPaymentAdapterRequestMessage);
        } catch (Exception ex) {
            log.warn("Failed to send XPaymentAdapterRequestMessage", ex);
            updateStatus(guid, PaymentStatus.NOT_SENT);
        }

        return response;
    }

    @Transactional
    public PaymentDto updatePayment(UUID id, PaymentDto paymentDto) {
        if (!paymentRepository.existsById(id)) {
            throw new ServiceException(ErrorMessage.PAYMENT_NOT_EXIST, id);
        }
        final Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setGuid(id);
        payment.setUpdatedAt(getNow());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public void deletePayment(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new ServiceException(PAYMENT_NOT_EXIST, id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus paymentStatus) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ServiceException(PAYMENT_NOT_EXIST, id));

        payment.setStatus(paymentStatus);
        payment.setUpdatedAt(getNow());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable) {
        final Specification<Payment> spec = PaymentFilterFactory.filter(filter);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

}
