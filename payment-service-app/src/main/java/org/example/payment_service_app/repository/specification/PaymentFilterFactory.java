package org.example.payment_service_app.repository.specification;

import org.example.payment_service_app.model.dto.PaymentFilterDto;
import org.example.payment_service_app.model.entity.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PaymentFilterFactory {

    public static Specification<Payment> filter(PaymentFilterDto filter) {

        Specification<Payment> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getStatus() != null) {
            spec = spec.and(PaymentSpecification.hasStatus(filter.getStatus()));
        }

        if (StringUtils.hasText(filter.getCurrency())) {
            spec = spec.and(PaymentSpecification.hasCurrency(filter.getCurrency()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() == null) {
            spec = spec.and(PaymentSpecification.amountGreaterThan(
                filter.getMinAmount()));
        }

        if (filter.getMinAmount() == null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecification.amountLessThan(
                filter.getMaxAmount()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecification.amountBetween(
                filter.getMinAmount(), filter.getMaxAmount()));
        }

        if (filter.getCreatedAfter() == null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecification.createdBefore(filter.getCreatedBefore()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() == null) {
            spec = spec.and(PaymentSpecification.createdAfter(filter.getCreatedAfter()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecification.createdBetween(filter.getCreatedAfter(), filter.getCreatedBefore()));
        }

        return spec;
    }

}
