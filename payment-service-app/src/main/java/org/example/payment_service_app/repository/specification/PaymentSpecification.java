package org.example.payment_service_app.repository.specification;

import org.example.payment_service_app.model.entity.Payment;
import org.example.payment_service_app.model.entity.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentSpecification {

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, cb) ->
            cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountGreaterThan(BigDecimal min) {
        return (root, query, cb) ->
            cb.greaterThan(root.get("amount"), min);
    }

    public static Specification<Payment> amountLessThan(BigDecimal max) {
        return (root, query, cb) ->
            cb.lessThan(root.get("amount"), max);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) ->
            cb.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> createdBefore(Instant before) {
        return (root, query, cb) ->
            cb.lessThan(root.get("createdAt"), before);
    }

    public static Specification<Payment> createdAfter(Instant after) {
        return (root, query, cb) ->
            cb.greaterThan(root.get("createdAt"), after);
    }

    public static Specification<Payment> createdBetween(Instant after, Instant before) {
        return (root, query, cb) ->
            cb.between(root.get("createdAt"), after, before);
    }

}