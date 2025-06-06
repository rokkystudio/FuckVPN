package fuck.system.vpn.ping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

object PacketSender {

    /**
     * Отправляет UDP-пакеты ("ping") на указанные IP-адреса через TUN-интерфейс.
     * Используется для имитации UDP-пинга (например, к OpenVPN серверам).
     *
     * @param fd файловый дескриптор туннеля
     * @param servers список IP-адресов назначения
     * @param sentTimes карта для хранения времени отправки по IP
     */
    suspend fun sendUdpToServers(
        fd: FileDescriptor,
        servers: List<Pair<String, Int>>,
        sentTimes: MutableMap<String, Long>
    ) {
        val output = FileOutputStream(fd)

        for ((ip, port) in servers) {
            val packet = buildUdpPacket(destIp = ip, destPort = port)
            sentTimes[ip] = System.currentTimeMillis()
            withContext(Dispatchers.IO) {
                output.write(packet) // потенциально блокирующий вызов
            }
            delay(100) // пауза между отправками
        }
    }

    /**
     * Отправляет TCP SYN-пакеты на указанные IP-адреса через TUN-интерфейс.
     * Используется для имитации TCP-пинга (по 3-way handshake).
     *
     * @param fd файловый дескриптор туннеля
     * @param servers список IP-адресов назначения
     * @param sentTimes карта для хранения времени отправки по IP
     */
    suspend fun sendTcpToServers(
        fd: FileDescriptor,
        servers: List<Pair<String, Int>>,
        sentTimes: MutableMap<String, Long>
    ) {
        val output = FileOutputStream(fd)

        for ((ip, port) in servers) {
            val packet = buildTcpSynPacket(destIp = ip, destPort = port)
            sentTimes[ip] = System.currentTimeMillis()
            withContext(Dispatchers.IO) {
                output.write(packet)
            }
            delay(100)
        }
    }

    /**
     * Формирует UDP-пакет с IP-заголовком.
     *
     * @param destIp IP-адрес назначения
     * @param destPort порт назначения
     * @return байтовый массив полного IP+UDP пакета
     */
    private fun buildUdpPacket(destIp: String, destPort: Int): ByteArray
    {
        val sourceIp = "10.0.0.2"
        val sourcePort = 54321

        val payload = "ping".toByteArray()

        val udpHeader = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).apply {
            putShort(sourcePort.toShort())             // Source port
            putShort(destPort.toShort())               // Destination port
            putShort((8 + payload.size).toShort())     // UDP length
            putShort(0)                                 // Checksum (опционально 0)
        }.array()

        val udpPacket = ByteBuffer.allocate(udpHeader.size + payload.size).apply {
            put(udpHeader)
            put(payload)
        }.array()

        val ipHeader = buildIpHeader(sourceIp, destIp, 17, udpPacket.size)
        return ipHeader + udpPacket
    }

    /**
     * Формирует TCP SYN-пакет с IP-заголовком.
     *
     * @param destIp IP-адрес назначения
     * @param destPort порт назначения
     * @return байтовый массив полного IP+TCP пакета
     */
    private fun buildTcpSynPacket(destIp: String, destPort: Int): ByteArray
    {
        val sourceIp = "10.0.0.2"
        val sourcePort = 54322

        val tcpHeader = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN).apply {
            putShort(sourcePort.toShort())     // Source port
            putShort(destPort.toShort())       // Destination port
            putInt(0)                           // Sequence number
            putInt(0)                           // Acknowledgment number
            put(0x50.toByte())                 // Data offset (5 << 4 = 80)
            put(0x02.toByte())                 // Flags: SYN
            putShort(64240.toShort())          // Window size
            putShort(0)                         // Checksum (опционально 0)
            putShort(0)                         // Urgent pointer
        }.array()

        val ipHeader = buildIpHeader(sourceIp, destIp, 6, tcpHeader.size)
        return ipHeader + tcpHeader
    }

    /**
     * Строит IP-заголовок (20 байт) с учётом размера вложенного протокола.
     *
     * @param sourceIp IP-адрес источника (обычно 10.0.0.2 в TUN)
     * @param destIp IP-адрес назначения
     * @param protocol номер протокола (UDP = 17, TCP = 6)
     * @param payloadLength длина вложенного протокольного сегмента (UDP или TCP)
     * @return байтовый массив IP-заголовка
     */
    private fun buildIpHeader(sourceIp: String, destIp: String, protocol: Int, payloadLength: Int): ByteArray
    {
        val ipHeader = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN).apply {
            put(0x45.toByte())                         // Версия (4) + IHL (5)
            put(0)                                     // DSCP + ECN
            putShort((20 + payloadLength).toShort())   // Полная длина пакета
            putShort(0)                                // Идентификатор
            putShort(0x4000.toShort())                 // Flags + Fragment offset (Don't Fragment)
            put(64.toByte())                           // TTL
            put(protocol.toByte())                     // Протокол
            putShort(0)                                // Контрольная сумма (временно 0)
            put(InetAddress.getByName(sourceIp).address)
            put(InetAddress.getByName(destIp).address)
        }.array()

        val checksum = ipChecksum(ipHeader)
        ipHeader[10] = (checksum.toInt() shr 8).toByte()
        ipHeader[11] = (checksum.toInt() and 0xFF).toByte()

        return ipHeader
    }

    /**
     * Вычисляет контрольную сумму IP-заголовка (RFC 791).
     *
     * @param header массив байтов IP-заголовка
     * @return 16-битная контрольная сумма
     */
    private fun ipChecksum(header: ByteArray): Short
    {
        var sum = 0
        var i = 0
        while (i < header.size) {
            val word = ((header[i].toInt() and 0xFF) shl 8) or (header[i + 1].toInt() and 0xFF)
            sum += word
            if ((sum and 0xFFFF0000.toInt()) != 0) {
                sum = (sum and 0xFFFF) + (sum ushr 16)
            }
            i += 2
        }
        return (sum.inv() and 0xFFFF).toShort()
    }
}
