package org.example.xpayment_adapter_app.async;

import org.springframework.stereotype.Component;

/**
 * Интерфейс обработчика входящих сообщений.
 *
 * @param <T> тип сообщения, который обрабатывается
 */
@Component
public interface MessageHandler<T extends Message> {
    /**
     * Обрабатывает переданное сообщение.
     *
     * @param message сообщение для обработки
     */
    void handle(T message);
}
