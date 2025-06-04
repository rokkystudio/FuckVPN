package fuck.system.vpn.ping

import kotlinx.coroutines.delay
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

object PacketSender
{
    suspend fun sendUdpToServers(fd: FileDescriptor, servers: List<String>) {
        val output = FileOutputStream(fd)

        for (ip in servers) {
            val packet = buildUdpPacket(destIp = ip, destPort = 1194) // Порт OpenVPN
            output.write(packet)
            delay(100) // Между пакетами
        }
    }

    private fun buildUdpPacket(destIp: String, destPort: Int): ByteArray {
        val sourceIp = "10.0.0.2"      // Наш IP в TUN
        val sourcePort = 54321         // Любой порт

        val payload = "ping".toByteArray()
        val udpHeader = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).apply {
            putShort(sourcePort.toShort())
            putShort(destPort.toShort())
            putShort((8 + payload.size).toShort()) // UDP length
            putShort(0) // Checksum (можно оставить 0, Android позволит)
        }.array()

        val udpPacket = ByteBuffer.allocate(udpHeader.size + payload.size).apply {
            put(udpHeader)
            put(payload)
        }.array()

        val ipHeader = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN).apply {
            put(0x45.toByte())                // Version + IHL
            put(0)                            // DSCP + ECN
            putShort((20 + udpPacket.size).toShort()) // Total length
            putShort(0)                       // ID
            putShort(0x4000.toShort())        // Flags + Fragment offset
            put(64.toByte())                  // TTL
            put(17.toByte())                  // Protocol: UDP (17)
            putShort(0)                       // Checksum (временно 0)
            put(InetAddress.getByName(sourceIp).address)
            put(InetAddress.getByName(destIp).address)
        }.array()

        val checksum = ipChecksum(ipHeader)
        ipHeader[10] = (checksum.toInt() shr 8).toByte()
        ipHeader[11] = (checksum.toInt() and 0xFF).toByte()

        return ipHeader + udpPacket
    }

    private fun ipChecksum(header: ByteArray): Short {
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