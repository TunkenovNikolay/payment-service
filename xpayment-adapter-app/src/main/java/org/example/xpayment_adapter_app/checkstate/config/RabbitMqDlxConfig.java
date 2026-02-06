package org.example.xpayment_adapter_app.checkstate.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqDlxConfig {

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange("payments.dlx");
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder
            .durable("payments.dead.queue")
            .build();
    }

    @Bean
    Binding dlxBinding() {
        return BindingBuilder
            .bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("payments.dead");

    }

}
