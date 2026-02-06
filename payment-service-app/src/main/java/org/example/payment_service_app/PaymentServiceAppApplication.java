package org.example.payment_service_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EnableResilientMethods
@EnableKafka
public class PaymentServiceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceAppApplication.class, args);
    }

}
