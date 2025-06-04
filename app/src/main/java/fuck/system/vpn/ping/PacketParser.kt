package fuck.system.vpn.ping

import android.util.Log

object PacketParser {

    fun parse(packet: ByteArray) {
        // Здесь ты можешь выделить IP-заголовок, проверить, был ли это ответ от сервера
        // можно распечатать debug-инфу
        Log.d("PacketParser", "Packet size: ${packet.size}")
        // TODO: Реализовать разбор UDP/TCP ответов
    }
}