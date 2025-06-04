package fuck.system.vpn.ping

import kotlinx.coroutines.delay
import java.io.FileDescriptor
import java.io.FileOutputStream

object PacketSender {

    suspend fun sendUdpToServers(fd: FileDescriptor, servers: List<String>) {
        val output = FileOutputStream(fd)

        for (ip in servers) {
            val packet = buildUdpPacket(destIp = ip, destPort = 1194) // Порт OpenVPN
            output.write(packet)
            delay(100) // Между пакетами
        }
    }

    private fun buildUdpPacket(destIp: String, destPort: Int): ByteArray {
        // Простая заглушка — сюда нужно вставить ручную сборку:
        // IP-заголовок + UDP-заголовок + payload
        return ByteArray(42) // TODO: Реализовать руками
    }
}