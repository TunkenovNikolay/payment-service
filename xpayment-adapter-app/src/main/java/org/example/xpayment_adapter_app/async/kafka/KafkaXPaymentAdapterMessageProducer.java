package org.example.xpayment_adapter_app.async.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.xpayment_adapter_app.async.AsyncSender;
import org.example.xpayment_adapter_app.dto.XPaymentAdapterResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaXPaymentAdapterMessageProducer implements AsyncSender<XPaymentAdapterResponseMessage> {
    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;
    private final String topic;

    public KafkaXPaymentAdapterMessageProducer(
            KafkaTemplate<String, XPaymentAdapterResponseMessage> template,
            @Value("${app.kafka.topics.xpayment-adapter.response:xpayment-adapter.responses}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterResponseMessage msg) {
        final String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency = {} ->topic = {} ",
            msg.getPaymentGuid(), msg.getAmount(), msg.getCurrency(), topic);
        template.send(topic, key, msg);
    }
}
