package org.example.payment_service_app.service;

import org.example.payment_service_app.exception.custom.PaymentNotFoundException;
import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.example.payment_service_app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void getPaymentById_shouldReturnPaymentDto_whenPaymentExists() {
        // given
        UUID paymentGuid = UUID.randomUUID();
        UUID inquiryGuid = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.50");
        String currency = "USD";

        Payment payment = createPayment(paymentGuid, inquiryGuid, amount, currency);
        PaymentDto expectedDto = createPaymentDto(paymentGuid, currency, amount);

        when(paymentRepository.findById(paymentGuid)).thenReturn(Optional.of(payment));
        when(paymentMapper.convertToDto(payment)).thenReturn(expectedDto);

        // when
        PaymentDto result = paymentService.getPaymentByUuid(paymentGuid);

        // then
        assertThat(result).isEqualTo(expectedDto);
        verify(paymentRepository).findById(paymentGuid);
        verify(paymentMapper).convertToDto(payment);
    }

    @Test
    void getPaymentById_shouldThrowException_whenPaymentNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        when(paymentRepository.findById(uuid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentByUuid(uuid))
            .isInstanceOf(PaymentNotFoundException.class)
            .hasMessage("Payment not found with id: " + uuid);

        verify(paymentRepository).findById(uuid);
    }

    @Test
    void getAllPayments_shouldReturnListOfDtos_whenPaymentsExist() {
        // given
        UUID paymentGuid1 = UUID.randomUUID();
        UUID inquiryGuid1 = UUID.randomUUID();
        BigDecimal amount1 = new BigDecimal("100.50");
        String currency1 = "USD";

        UUID paymentGuid2 = UUID.randomUUID();
        UUID inquiryGuid2 = UUID.randomUUID();
        BigDecimal amount2 = new BigDecimal("100.50");
        String currency2 = "RUB";

        Payment payment1 = createPayment(paymentGuid1, inquiryGuid1, amount1, currency1);
        Payment payment2 = createPayment(paymentGuid2, inquiryGuid2, amount2, currency2);
        List<Payment> payments = List.of(payment1, payment2);

        PaymentDto dto1 = createPaymentDto(paymentGuid1, currency1, amount1);
        PaymentDto dto2 = createPaymentDto(paymentGuid2, currency2, amount2);
        List<PaymentDto> expectedDto = List.of(dto1, dto2);

        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.convertToDtoList(payments)).thenReturn(expectedDto);

        // when
        List<PaymentDto> result = paymentService.getAllPayments();

        // then
        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
        verify(paymentRepository).findAll();
        verify(paymentMapper).convertToDtoList(payments);
    }

    @Test
    void getAllPayments_shouldReturnEmptyList_whenNoPayments() {
        // given
        when(paymentRepository.findAll()).thenReturn(List.of());
        when(paymentMapper.convertToDtoList(List.of())).thenReturn(List.of());

        // when
        List<PaymentDto> result = paymentService.getAllPayments();

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository).findAll();
        verify(paymentMapper).convertToDtoList(List.of());
    }

    private Payment createPayment(UUID guid, UUID inquiryRefId, BigDecimal amount, String currency) {
        Payment payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(PaymentStatus.PENDING); // или другой начальный статус
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());
        return payment;
    }

    private PaymentDto createPaymentDto(UUID guid, String currency, BigDecimal amount) {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(guid);
        dto.setCurrency(currency);
        dto.setAmount(amount);
        return dto;
    }
}