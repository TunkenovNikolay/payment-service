package org.example.payment_service_app.mapper;

import org.example.payment_service_app.model.dto.PaymentDto;
import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentMapperTest {

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void toDtoTestWithAllStatuses(PaymentStatus paymentStatus) {
        //given
        var now = OffsetDateTime.now();
        var expected = new Payment();
        expected.setGuid(UUID.randomUUID());
        expected.setAmount(new BigDecimal("123.45"));
        expected.setCurrency("USD");
        expected.setInquiryRefId(UUID.randomUUID());
        expected.setStatus(paymentStatus);
        expected.setCreatedAt(now);
        expected.setUpdatedAt(now);

        //when
        var actual = mapper.toDto(expected);

        //then
        assertNotNull(actual);
        assertEquals(expected.getGuid(), actual.getGuid());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(expected.getInquiryRefId(), actual.getInquiryRefId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());

    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void toEntityTestWithAllStatuses(PaymentStatus paymentStatus) {
        //given
        var now = OffsetDateTime.now();
        PaymentDto expected = PaymentDto.builder()
            .guid(UUID.randomUUID())
            .note("note")
            .amount(new BigDecimal("123.45"))
            .currency("USD")
            .inquiryRefId(UUID.randomUUID())
            .status(paymentStatus)
            .createdAt(now)
            .updatedAt(now)
            .build();

        //when
        var actual = mapper.toEntity(expected);

        //then
        assertNotNull(actual);
        assertEquals(expected.getGuid(), actual.getGuid());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(expected.getInquiryRefId(), actual.getInquiryRefId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());

    }
}
