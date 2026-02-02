package org.example.xpayment_adapter_app.async.kafka;

import org.example.xpayment_adapter_app.async.AsyncListener;
import org.example.xpayment_adapter_app.async.MessageHandler;
import org.example.xpayment_adapter_app.async.XPaymentAdapterRequestMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaXPaymentAdapterMessageListener implements AsyncListener<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(KafkaXPaymentAdapterMessageListener.class);
    private final MessageHandler<XPaymentAdapterRequestMessage> handler;
    private final ObjectMapper objectMapper;

    public KafkaXPaymentAdapterMessageListener(MessageHandler<XPaymentAdapterRequestMessage> handler, ObjectMapper objectMapper) {
        this.handler = handler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(
        topics = "${app.kafka.topics.xpayment-adapter.request}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory")  // ← укажите явно!
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String json = record.value();
            XPaymentAdapterRequestMessage message = objectMapper.readValue(json, XPaymentAdapterRequestMessage.class);

            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request", e);
            throw e;
        }
    }
}
