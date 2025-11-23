package org.example.payment_service_app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Payment {
    Long id;
    Double value;
    String name;
    LocalDate date;
}
