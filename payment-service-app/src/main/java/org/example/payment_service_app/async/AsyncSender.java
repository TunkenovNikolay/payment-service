package org.example.payment_service_app.async;

/**
 * Интерфейс отправки сообщений для асинхронной обработки
 *
 * @param <T> тип сообщения, которое отправляется
 */
public interface AsyncSender<T extends Message> {
    /**
     * Отправляет сообщение.
     *
     * @param message сообщение для отправки
     */
    void send(T message);
}
