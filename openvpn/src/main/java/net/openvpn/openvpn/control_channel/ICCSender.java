package net.openvpn.openvpn.control_channel;

/**
 * Интерфейс отправки сообщений в пользовательский канал управления (Custom Control Channel).
 */
public interface ICCSender {
    /**
     * Отправка сообщения в канал управления.
     *
     * @param protocol Протокол сообщения (например, "dpc1").
     * @param payload  Содержимое сообщения в виде строки.
     */
    void send_app_control_channel_msg(String protocol, String payload);
}