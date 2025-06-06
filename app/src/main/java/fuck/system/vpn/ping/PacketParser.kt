package fuck.system.vpn.ping

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.util.Log

object PacketParser
{
    var sentTimes: Map<String, Long>? = null          // Время отправки пакетов по IP
    var clientMessenger: Messenger? = null            // Связь с UI через Messenger
    var rtts: MutableMap<String, Int>? = null         // Словарь для сохранения финального RTT по IP

    /**
     * Основной метод обработки входящих IP-пакетов.
     * Фильтрует по протоколу, извлекает IP-источник, проверяет TCP-флаги,
     * вычисляет RTT и отправляет результат через Messenger.
     */
    fun parse(packet: ByteArray) {
        // Минимальный размер IP-пакета с UDP или TCP (20 байт IP + заголовок)
        if (packet.size < 28) return

        val protocol = getProtocol(packet)
        if (protocol != Protocol.UDP && protocol != Protocol.TCP) return

        val sourceIp = extractSourceIp(packet)

        // TCP: проверяем, что это SYN-ACK, иначе игнорируем
        if (protocol == Protocol.TCP && !isTcpSynAck(packet)) return

        val rtt = calculateRtt(sourceIp)
        if (rtt < 0) return

        // Сохраняем результат в общий словарь
        rtts?.put(sourceIp, rtt)

        logResult(sourceIp, protocol, rtt)
        sendRttResult(sourceIp, rtt)
    }

    /**
     * Типы IP-протоколов, которые нас интересуют.
     */
    private enum class Protocol { TCP, UDP, UNKNOWN }

    /**
     * Извлекает протокол из IP-заголовка (байт 9).
     * 6 = TCP, 17 = UDP.
     */
    private fun getProtocol(packet: ByteArray): Protocol {
        val protocolByte = packet[9].toInt() and 0xFF
        return when (protocolByte) {
            6 -> Protocol.TCP
            17 -> Protocol.UDP
            else -> Protocol.UNKNOWN
        }
    }

    /**
     * Извлекает IP-адрес источника из байт 12–15 IP-заголовка.
     */
    private fun extractSourceIp(packet: ByteArray): String {
        return listOf(packet[12], packet[13], packet[14], packet[15])
            .joinToString(".") { it.toUByte().toString() }
    }

    /**
     * Проверяет, что TCP-пакет является ответом SYN-ACK.
     * Используется для определения, что удалённый хост действительно ответил на SYN.
     */
    private fun isTcpSynAck(packet: ByteArray): Boolean {
        val ipHeaderLength = (packet[0].toInt() and 0x0F) * 4

        // Должно быть достаточно байт для TCP-заголовка
        if (packet.size < ipHeaderLength + 14) return false

        val flags = packet[ipHeaderLength + 13].toInt() and 0xFF
        val syn = flags and 0x02 != 0
        val ack = flags and 0x10 != 0

        return syn && ack
    }

    /**
     * Вычисляет RTT (в миллисекундах) между отправкой и получением по IP-адресу.
     */
    private fun calculateRtt(sourceIp: String): Int {
        val now = System.currentTimeMillis()
        val sentAt = sentTimes?.get(sourceIp)
        return if (sentAt != null) (now - sentAt).toInt() else -1
    }

    /**
     * Отправляет результат пинга обратно через Messenger.
     */
    private fun sendRttResult(ip: String, ping: Int) {
        val msg = Message.obtain().apply {
            data = Bundle().apply {
                putString("ip", ip)
                putInt("ping", ping)
            }
        }
        clientMessenger?.send(msg)
    }

    /**
     * Логирует результат пинга в Logcat.
     */
    private fun logResult(ip: String, protocol: Protocol, ping: Int) {
        Log.d("PacketParser", "Ответ от $ip (${protocol.name}), RTT = ${ping}мс")
    }
}
