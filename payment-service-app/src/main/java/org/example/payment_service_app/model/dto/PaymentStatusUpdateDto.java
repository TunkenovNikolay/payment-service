package org.example.payment_service_app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.payment_service_app.model.entity.PaymentStatus;

@Setter
@Getter
public class PaymentStatusUpdateDto {

    @NotNull
    private PaymentStatus status;

}
