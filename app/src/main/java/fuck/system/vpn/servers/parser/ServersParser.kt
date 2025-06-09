package fuck.system.vpn.servers.parser

import android.util.Base64
import fuck.system.vpn.servers.server.ServerGeo.getCountryByIp
import fuck.system.vpn.servers.server.ServerItem
import java.io.BufferedReader
import java.net.InetAddress

object ServersParser {

    /**
     * Декодирует base64-строку конфигурации.
     * Если не удалось (например, она не в base64), возвращает исходную строку.
     */
    fun decodeConfig(input: String): String {
        return try {
            String(Base64.decode(input, Base64.DEFAULT))
        } catch (e: Exception) {
            input
        }
    }

    /**
     * Извлекает хост (IP или домен) из строки конфигурации OpenVPN.
     */
    fun getRemoteHost(ovpn: String): String? {
        val match = Regex("""remote\s+([^\s]+)""").find(ovpn)
        return match?.groupValues?.get(1)
    }

    /**
     * Проверяет, является ли строка корректным IPv4-адресом.
     */
    fun isValidIp(address: String): Boolean {
        val regex = Regex("""\d{1,3}(\.\d{1,3}){3}""")
        return regex.matches(address)
    }

    /**
     * Пытается разрешить доменное имя в IP-адрес.
     * Возвращает null, если резолвинг не удался.
     */
    fun resolveHostToIp(host: String): String? {
        return try {
            InetAddress.getByName(host).hostAddress
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Извлекает IP-адрес из ovpn-конфигурации.
     * Если в конфиге указан домен — пробует его разрешить.
     * Если не удалось, использует значение из CSV (parts[1]).
     */
    fun getIpFromOvpn(ovpn: String, fallbackIp: String?): String? {
        val host = getRemoteHost(ovpn) ?: return fallbackIp
        return when {
            isValidIp(host) -> host
            fallbackIp != null && isValidIp(fallbackIp) -> fallbackIp
            else -> resolveHostToIp(host)
        }
    }

    /**
     * Парсит порт из строки remote в ovpn-конфиге.
     * Пример строки: "remote vpn.example.com 1194"
     */
    fun getPortFromOvpn(ovpn: String): Int? {
        val regex = Regex("""remote\s+[\w.-]+ (\d+)""")
        val match = regex.find(ovpn)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    /**
     * Извлекает протокол из ovpn-конфига ("udp" или "tcp").
     */
    fun getProtoFromOvpn(ovpn: String): String? {
        val match = Regex("""proto\s+(udp|tcp)""").find(ovpn)
        return match?.groupValues?.get(1)
    }

    /**
     * Парсит CSV-файл VPNGate и возвращает список ServerItem.
     */
    fun parseCsv(reader: BufferedReader): List<ServerItem> {
        val servers = mutableListOf<ServerItem>()
        var line: String?
        var lineCount = 0

        while (reader.readLine().also { line = it } != null) {
            lineCount++
            if (lineCount <= 2 || line.isNullOrBlank()) continue

            parseCsvLine(line)?.let { servers.add(it) }
        }

        reader.close()
        return servers
    }

    /**
     * Парсит строку CSV и возвращает ServerItem или null.
     */
    fun parseCsvLine(line: String): ServerItem? {
        val parts = line.split(",")
        if (parts.size < 15) return null

        val ovpnRaw = parts[14]
        val ovpn = decodeConfig(ovpnRaw)

        val fallbackIp = parts[1]
        val ip = getIpFromOvpn(ovpn, fallbackIp) ?: return null
        val port = getPortFromOvpn(ovpn)
        val proto = getProtoFromOvpn(ovpn)
        val ping = parts[3].toIntOrNull()

        val csvCountry = parts[6].takeIf { it.isNotBlank() }?.lowercase()
        val country = csvCountry ?: getCountryByIp(ip)

        val name = parts[2].trim().takeIf { it.isNotBlank() } ?: ip

        return ServerItem(
            name = name,
            ip = ip,
            port = port,
            country = country,
            ping = ping,
            favorite = false,
            ovpn = ovpn,
            proto = proto
        )
    }
}
