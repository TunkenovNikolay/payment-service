package org.example.payment_service_app.service;

import org.example.payment_service_app.mapper.PaymentMapper;
import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.example.payment_service_app.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.payment_service_app.util.TimeUtil.getNow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment paymentUsdReceived;
    private Payment paymentEurPending;
    private Payment paymentUsdApproved;
    private Payment paymentGbpDeclined;

    private Pageable pageable;
    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        OffsetDateTime offsetNow = now.atOffset(ZoneOffset.UTC);
        pageable = PageRequest.of(0, 10);

        paymentUsdReceived = createPayment(
            "USD", new BigDecimal("100.00"),
            PaymentStatus.RECEIVED, offsetNow.minusDays(2)
        );
        paymentEurPending = createPayment(
            "EUR", new BigDecimal("200.00"),
            PaymentStatus.PENDING, offsetNow.minusDays(1)
        );
        paymentUsdApproved = createPayment(
            "USD", new BigDecimal("300.00"),
            PaymentStatus.APPROVED, offsetNow
        );
        paymentGbpDeclined = createPayment(
            "GBP", new BigDecimal("400.00"),
            PaymentStatus.DECLINED, offsetNow.minusHours(3)
        );
    }

    @Test
    void search_WithEmptyFilter_ReturnsAllPayments() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder().build();
        List<Payment> payments = Arrays.asList(
            paymentUsdReceived, paymentEurPending,
            paymentUsdApproved, paymentGbpDeclined
        );
        Page<Payment> paymentPage = new PageImpl<>(payments, pageable, payments.size());

        when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentUsdReceived)).thenReturn(createDtoFromPayment(paymentUsdReceived));
        when(paymentMapper.toDto(paymentEurPending)).thenReturn(createDtoFromPayment(paymentEurPending));
        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));
        when(paymentMapper.toDto(paymentGbpDeclined)).thenReturn(createDtoFromPayment(paymentGbpDeclined));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertNotNull(result);
        assertEquals(4, result.getContent().size());

        // Проверяем вызовы
        verify(paymentRepository).findAll(isA(Specification.class), eq(pageable));
        verify(paymentMapper, times(4)).toDto(any(Payment.class));
    }

    @Test
    void search_WithCurrencyFilter_ReturnsFilteredPayments() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .currency("USD")
            .build();

        List<Payment> usdPayments = Arrays.asList(paymentUsdReceived, paymentUsdApproved);
        Page<Payment> paymentPage = new PageImpl<>(usdPayments, pageable, usdPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        // Мокаем маппер с ручным созданием DTO
        when(paymentMapper.toDto(paymentUsdReceived)).thenReturn(createDtoFromPayment(paymentUsdReceived));
        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
            .allMatch(dto -> "USD".equals(dto.getCurrency())));
    }

    @Test
    void search_WithStatusFilter_ReturnsFilteredPayments() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .status(PaymentStatus.APPROVED)
            .build();

        List<Payment> filteredPayments = List.of(paymentUsdApproved);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(PaymentStatus.APPROVED, result.getContent().getFirst().getStatus());
    }

    @Test
    void search_WithAmountRangeFilter_ReturnsFilteredPayments() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .minAmount(new BigDecimal("150.00"))
            .maxAmount(new BigDecimal("350.00"))
            .build();

        List<Payment> filteredPayments = Arrays.asList(paymentEurPending, paymentUsdApproved);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentEurPending)).thenReturn(createDtoFromPayment(paymentEurPending));
        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
            .allMatch(dto -> dto.getAmount().compareTo(new BigDecimal("150.00")) >= 0 &&
                dto.getAmount().compareTo(new BigDecimal("350.00")) <= 0));
    }

    @Test
    void search_WithDateFilter_ReturnsFilteredPayments() {
        // Given
        Instant startDate = now.minus(2, ChronoUnit.DAYS);
        Instant endDate = now.plus(1, ChronoUnit.DAYS);

        PaymentFilterDto filter = PaymentFilterDto.builder()
            .createdAfter(startDate)
            .createdBefore(endDate)
            .build();

        List<Payment> filteredPayments = Arrays.asList(paymentEurPending, paymentUsdApproved, paymentGbpDeclined);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentEurPending)).thenReturn(createDtoFromPayment(paymentEurPending));
        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));
        when(paymentMapper.toDto(paymentGbpDeclined)).thenReturn(createDtoFromPayment(paymentGbpDeclined));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(3, result.getContent().size());
        assertTrue(result.getContent().stream()
            .allMatch(dto -> dto.getCreatedAt().isAfter(startDate.atOffset(ZoneOffset.UTC)) &&
                dto.getCreatedAt().isBefore(endDate.atOffset(ZoneOffset.UTC))));
    }

    @Test
    void search_WithCombinedFilters_ReturnsCorrectPayments() {
        // Given
        Instant startDate = now.minus(2, ChronoUnit.DAYS);

        PaymentFilterDto filter = PaymentFilterDto.builder()
            .currency("USD")
            .status(PaymentStatus.APPROVED)
            .minAmount(new BigDecimal("200.00"))
            .createdAfter(startDate)
            .build();

        List<Payment> filteredPayments = List.of(paymentUsdApproved);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentUsdApproved)).thenReturn(createDtoFromPayment(paymentUsdApproved));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        PaymentDto dto = result.getContent().getFirst();
        assertEquals("USD", dto.getCurrency());
        assertEquals(PaymentStatus.APPROVED, dto.getStatus());
        assertTrue(dto.getAmount().compareTo(new BigDecimal("200.00")) >= 0);
        assertTrue(dto.getCreatedAt().isAfter(startDate.atOffset(ZoneOffset.UTC)));
    }

    @Test
    void search_WithNoMatchingResults_ReturnsEmptyPage() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .currency("JPY")
            .status(PaymentStatus.NOT_SENT)
            .build();

        Page<Payment> paymentPage = new PageImpl<>(List.of(), pageable, 0);

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void search_WithNullFilter_ReturnsAllPayments() {
        // Given
        List<Payment> payments = Arrays.asList(paymentUsdReceived, paymentEurPending);
        Page<Payment> paymentPage = new PageImpl<>(payments, pageable, payments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentUsdReceived)).thenReturn(createDtoFromPayment(paymentUsdReceived));
        when(paymentMapper.toDto(paymentEurPending)).thenReturn(createDtoFromPayment(paymentEurPending));

        // When
        Page<PaymentDto> result = paymentService.search(null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void search_WithStatusRECEIVED_ReturnsCorrectPayment() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .status(PaymentStatus.RECEIVED)
            .build();

        List<Payment> filteredPayments = List.of(paymentUsdReceived);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentUsdReceived)).thenReturn(createDtoFromPayment(paymentUsdReceived));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(PaymentStatus.RECEIVED, result.getContent().getFirst().getStatus());
    }

    @Test
    void search_WithStatusPENDING_ReturnsCorrectPayment() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .status(PaymentStatus.PENDING)
            .build();

        List<Payment> filteredPayments = List.of(paymentEurPending);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentEurPending)).thenReturn(createDtoFromPayment(paymentEurPending));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(PaymentStatus.PENDING, result.getContent().getFirst().getStatus());
    }

    @Test
    void search_WithStatusDECLINED_ReturnsCorrectPayment() {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .status(PaymentStatus.DECLINED)
            .build();

        List<Payment> filteredPayments = List.of(paymentGbpDeclined);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(paymentGbpDeclined)).thenReturn(createDtoFromPayment(paymentGbpDeclined));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(PaymentStatus.DECLINED, result.getContent().getFirst().getStatus());
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void search_WithEachStatus_ReturnsCorrectPayments(PaymentStatus status) {
        // Given
        PaymentFilterDto filter = PaymentFilterDto.builder()
            .status(status)
            .build();

        Payment matchingPayment = getPaymentByStatus(status);

        // Пропускаем тест, если нет тестовых данных для этого статуса
        if (matchingPayment == null) {
            return;
        }

        List<Payment> filteredPayments = List.of(matchingPayment);
        Page<Payment> paymentPage = new PageImpl<>(filteredPayments, pageable, filteredPayments.size());

        when(paymentRepository.findAll(isA(Specification.class), eq(pageable)))
            .thenReturn(paymentPage);

        when(paymentMapper.toDto(matchingPayment)).thenReturn(createDtoFromPayment(matchingPayment));

        // When
        Page<PaymentDto> result = paymentService.search(filter, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(status, result.getContent().getFirst().getStatus());
    }

    @Test
    void createPaymentTest() {
        // Given
        PaymentDto expected = createDtoFromPayment(paymentUsdReceived);
        expected.setGuid(null);

        Payment paymentToSave = createPaymentFromDto(expected);
        paymentToSave.setGuid(UUID.randomUUID());
        paymentToSave.setCreatedAt(getNow());
        paymentToSave.setUpdatedAt(getNow());

        Payment savedPayment = createPaymentFromDto(expected);
        savedPayment.setGuid(paymentToSave.getGuid());
        savedPayment.setCreatedAt(getNow());
        savedPayment.setUpdatedAt(getNow());
        savedPayment.setStatus(PaymentStatus.RECEIVED);

        when(paymentMapper.toEntity(expected))
            .thenReturn(paymentToSave);
        when(paymentRepository.save(paymentToSave))
            .thenReturn(savedPayment);
        when(paymentMapper.toDto(savedPayment))
            .thenReturn(createDtoFromPayment(savedPayment));

        // When
        PaymentDto actual = paymentService.createPayment(expected);

        // Then
        assertNotNull(actual);
        assertNotNull(actual.getGuid());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(PaymentStatus.RECEIVED, actual.getStatus());

        verify(paymentRepository).save(paymentToSave);
    }

    @Test
    void updatePaymentTest() {
        // Given
        UUID id = UUID.randomUUID();
        PaymentDto requestDto = createDtoFromPayment(paymentUsdReceived);
        requestDto.setGuid(null);

        // Создаем Payment, который будет возвращен из маппера
        Payment paymentFromMapper = createPaymentFromDto(requestDto);
        paymentFromMapper.setGuid(id);

        // Создаем сохраненный Payment
        Payment savedPayment = createPaymentFromDto(requestDto);
        savedPayment.setGuid(id);
        savedPayment.setUpdatedAt(getNow());

        // Мокаем вызовы
        when(paymentRepository.existsById(id))
            .thenReturn(true);
        when(paymentMapper.toEntity(requestDto))
            .thenReturn(paymentFromMapper);
        when(paymentRepository.save(paymentFromMapper))
            .thenReturn(savedPayment);
        when(paymentMapper.toDto(savedPayment))
            .thenReturn(createDtoFromPayment(savedPayment));

        // When
        PaymentDto actual = paymentService.updatePayment(id, requestDto);

        // Then
        assertNotNull(actual);
        assertEquals(id, actual.getGuid());
        assertEquals(requestDto.getAmount(), actual.getAmount());
        assertEquals(requestDto.getCurrency(), actual.getCurrency());

        // Проверяем правильные вызовы
        verify(paymentRepository).existsById(id);
        verify(paymentMapper).toEntity(requestDto);
        verify(paymentRepository).save(paymentFromMapper);
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void deletePaymentTest() {
        // Given
        UUID paymentId = UUID.randomUUID();
        Payment payment = paymentUsdReceived;
        payment.setGuid(paymentId);

        when(paymentRepository.existsById(paymentId)).thenReturn(true);

        // When
        paymentService.deletePayment(paymentId);

        // Then
        verify(paymentRepository).existsById(paymentId);
        verify(paymentRepository).deleteById(paymentId);
    }


    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void updateStatusTest(PaymentStatus status) {
        // Given
        UUID id = UUID.randomUUID();

        Payment existingPayment = new Payment();
        existingPayment.setGuid(id);
        existingPayment.setStatus(PaymentStatus.PENDING); // начальный статус

        Payment updatedPayment = new Payment();
        updatedPayment.setGuid(id);
        updatedPayment.setStatus(status);
        updatedPayment.setUpdatedAt(getNow());

        PaymentDto expectedDto = createDtoFromPayment(updatedPayment);

        when(paymentRepository.findById(id))
            .thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class)))
            .thenReturn(updatedPayment);
        when(paymentMapper.toDto(updatedPayment))
            .thenReturn(expectedDto);

        // When
        PaymentDto actual = paymentService.updateStatus(id, status);

        // Then
        assertNotNull(actual);
        assertEquals(id, actual.getGuid());
        assertEquals(status, actual.getStatus());

        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(updatedPayment);
    }

    // Вспомогательные методы
    private Payment createPayment(String currency, BigDecimal amount,
                                  PaymentStatus status, OffsetDateTime createdAt) {
        Payment payment = new Payment();
        payment.setGuid(UUID.randomUUID());
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setTransactionRefId(UUID.randomUUID());
        payment.setStatus(status);
        payment.setNote("Test payment - " + status);
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(createdAt);
        return payment;
    }

    private PaymentDto createDtoFromPayment(Payment payment) {
        return PaymentDto.builder()
            .guid(payment.getGuid())
            .inquiryRefId(payment.getInquiryRefId())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .transactionRefId(payment.getTransactionRefId())
            .status(payment.getStatus())
            .note(payment.getNote())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }

    private Payment createPaymentFromDto(PaymentDto paymentDto) {
        return Payment.builder()
            .guid(paymentDto.getGuid())
            .inquiryRefId(paymentDto.getInquiryRefId())
            .amount(paymentDto.getAmount())
            .currency(paymentDto.getCurrency())
            .transactionRefId(paymentDto.getTransactionRefId())
            .status(paymentDto.getStatus())
            .note(paymentDto.getNote())
            .createdAt(paymentDto.getCreatedAt())
            .updatedAt(paymentDto.getUpdatedAt())
            .build();
    }

    private Payment getPaymentByStatus(PaymentStatus status) {
        return switch (status) {
            case RECEIVED -> paymentUsdReceived;
            case PENDING -> paymentEurPending;
            case APPROVED -> paymentUsdApproved;
            case DECLINED -> paymentGbpDeclined;
            case NOT_SENT -> null;
        };
    }
}