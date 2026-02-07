package org.example.xpayment_adapter_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class XPaymentAdapterAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(XPaymentAdapterAppApplication.class, args);
    }
}
