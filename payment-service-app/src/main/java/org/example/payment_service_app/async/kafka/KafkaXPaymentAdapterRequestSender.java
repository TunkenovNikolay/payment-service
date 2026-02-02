package org.example.payment_service_app.async.kafka;

import org.example.payment_service_app.async.AsyncSender;
import org.example.payment_service_app.async.XPaymentAdapterRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaXPaymentAdapterRequestSender implements AsyncSender<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(KafkaXPaymentAdapterRequestSender.class);
    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> template;
    private final String topic;

    public KafkaXPaymentAdapterRequestSender(
            KafkaTemplate<String, XPaymentAdapterRequestMessage> template,
            @Value("${app.kafka.topics.xpayment-adapter.request:xpayment-adapter.requests}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage msg) {
        final String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency = {} ->topic = {} ",
            msg.getPaymentGuid(), msg.getAmount(),
            msg.getCurrency(), topic);
        template.send(topic, key, msg);
    }
}