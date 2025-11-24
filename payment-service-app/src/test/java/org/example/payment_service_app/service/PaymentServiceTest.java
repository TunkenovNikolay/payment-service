package org.example.payment_service_app.service;

import org.example.payment_service_app.EntityExceptions.PaymentNotFoundException;
import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    private final Instant now = Instant.now();

    @Test
    void getPaymentById_shouldReturnPaymentDto_whenPaymentExists() {
        // given
        long paymentId = 1L;
        Payment payment = createPayment(paymentId, "Test Payment", 100.0);
        PaymentDto expectedDto = createPaymentDto(paymentId, "Test Payment", 100.0);

        when(paymentRepository.getPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.convertToDto(payment)).thenReturn(expectedDto);

        // when
        PaymentDto result = paymentService.getPaymentById(paymentId);

        // then
        assertThat(result).isEqualTo(expectedDto);
        verify(paymentRepository).getPaymentById(paymentId);
        verify(paymentMapper).convertToDto(payment);
    }

    @Test
    void getPaymentById_shouldThrowException_whenPaymentNotFound() {
        // given
        long paymentId = 999L;
        when(paymentRepository.getPaymentById(paymentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentById(paymentId))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessage("Payment not found with id: " + paymentId);

        verify(paymentRepository).getPaymentById(paymentId);
    }

    @Test
    void getAllPayments_shouldReturnListOfDtos_whenPaymentsExist() {
        // given
        Payment payment1 = createPayment(1L, "Payment 1", 100.0);
        Payment payment2 = createPayment(2L, "Payment 2", 200.0);
        List<Payment> payments = List.of(payment1, payment2);

        PaymentDto dto1 = createPaymentDto(1L, "Payment 1", 100.0);
        PaymentDto dto2 = createPaymentDto(2L, "Payment 2", 200.0);
        List<PaymentDto> expectedDtos = List.of(dto1, dto2);

        when(paymentRepository.getAllPayments()).thenReturn(payments);
        when(paymentMapper.convertToDtoList(payments)).thenReturn(expectedDtos);

        // when
        List<PaymentDto> result = paymentService.getAllPayments();

        // then
        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
        verify(paymentRepository).getAllPayments();
        verify(paymentMapper).convertToDtoList(payments);
    }

    @Test
    void getAllPayments_shouldReturnEmptyList_whenNoPayments() {
        // given
        when(paymentRepository.getAllPayments()).thenReturn(List.of());
        when(paymentMapper.convertToDtoList(List.of())).thenReturn(List.of());

        // when
        List<PaymentDto> result = paymentService.getAllPayments();

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository).getAllPayments();
        verify(paymentMapper).convertToDtoList(List.of());
    }

    @Test
    void getAllPayments_shouldReturnEmptyList_whenRepositoryReturnsNull() {
        // given
        when(paymentRepository.getAllPayments()).thenReturn(null);
        when(paymentMapper.convertToDtoList(null)).thenReturn(List.of());

        // when
        List<PaymentDto> result = paymentService.getAllPayments();

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository).getAllPayments();
        verify(paymentMapper).convertToDtoList(null);
    }

    private Payment createPayment(Long id, String name, Double value) {
        return new Payment(id, value, name, now);
    }

    private PaymentDto createPaymentDto(Long id, String name, Double value) {
        PaymentDto dto = new PaymentDto();
        dto.setId(id);
        dto.setName(name);
        dto.setValue(value);
        return dto;
    }
}