package fuck.system.vpn.ping

import android.util.Log

object PacketParser
{
    fun parse(packet: ByteArray) {
        if (packet.size < 28) return

        val ipHeaderLength = (packet[0].toInt() and 0x0F) * 4
        val protocol = packet[9].toInt() and 0xFF

        val sourceIp = "${packet[12].toUByte()}.${packet[13].toUByte()}." +
                "${packet[14].toUByte()}.${packet[15].toUByte()}"

        val destIp = "${packet[16].toUByte()}.${packet[17].toUByte()}." +
                "${packet[18].toUByte()}.${packet[19].toUByte()}"

        if (protocol == 17) { // UDP
            val srcPort = ((packet[ipHeaderLength].toInt() and 0xFF) shl 8) or
                    (packet[ipHeaderLength + 1].toInt() and 0xFF)

            Log.d("PacketParser", "Ответ от $sourceIp:$srcPort до $destIp (UDP)")
        }
    }
}