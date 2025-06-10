package fuck.system.vpn.servers.parser

import android.util.Base64
import fuck.system.vpn.servers.server.ServerGeo.getCountryByIp
import fuck.system.vpn.servers.server.ServerItem
import java.io.BufferedReader
import java.net.InetAddress

/**
 * Утилитный парсер VPN-конфигураций и CSV-данных от VPNGate.
 *
 * Содержит функции для:
 * - декодирования base64-конфигураций,
 * - извлечения ключевых параметров из OpenVPN-файлов (host, ip, port, proto, country),
 * - преобразования строк CSV в объекты ServerItem,
 * - разрешения доменных имён в IP-адреса.
 *
 * Используется для обработки как публичных серверов VPNGate, так и вручную добавленных конфигураций.
 */
object ServersParser
{
    /**
     * Декодирует base64-строку конфигурации.
     * Если не удалось (например, она не в base64), возвращает исходную строку.
     */
    fun decodeConfig(input: String): String {
        return try {
            String(Base64.decode(input, Base64.DEFAULT))
        } catch (e: Exception) {
            e.printStackTrace()
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
    fun getIpFromHost(host: String): String? {
        return try {
            InetAddress.getByName(host).hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Извлекает порт из строки remote в ovpn-конфиге.
     * Пример строки: "remote vpn.example.com 1194"
     */
    fun getPortFromOvpn(ovpn: String): Int? {
        val regex = Regex("""remote\s+[\w.-]+\s+(\d+)""")
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
     * Извлекает страну из строки #COUNTRY=XX (без учёта регистра).
     */
    fun getCountryFromOvpn(ovpn: String): String? {
        val regex = Regex("""(?i)#.*COUNTRY\s*=\s*([a-z]{2})""")
        return regex.find(ovpn)?.groupValues?.get(1)?.lowercase()
    }

    /**
     * Парсит CSV-файл VPNGate и возвращает список ServerItem.
     */
    fun parseCsv(reader: BufferedReader): List<ServerItem>
    {
        val servers = mutableListOf<ServerItem>()
        var lineCount = 0

        while (true) {
            val line = reader.readLine() ?: break
            lineCount++
            if (lineCount <= 2 || line.isBlank()) continue

            parseCsvLine(line)?.let { servers.add(it) }
        }

        reader.close()
        return servers
    }

    /**
     * Парсит строку CSV и возвращает ServerItem или null.
     * Здесь происходит основная логика извлечения данных из ovpn-конфигурации,
     * включая приоритет IP-адреса: сначала берётся из remote, потом из fallback, при необходимости — резолвится домен.
     */
    fun parseCsvLine(line: String): ServerItem?
    {
        val parts = line.split(",")
        val ovpn = parseCsvConfig(parts)
        if (ovpn == null) return null

        val address = parseCsvIp(parts)
        val host = getRemoteHost(ovpn)

        val ip = resolveIp(host, address)
        if (ip == null) return null

        val port = getPortFromOvpn(ovpn)
            ?: parseCsvPort(parts)

        val proto = getProtoFromOvpn(ovpn)
        val ping = parseCsvPing(parts)

        val country = getCountryFromOvpn(ovpn)
            ?: parseCsvCountry(parts)
            ?: getCountryByIp(ip)

        val name = parseCsvName(parts)
            ?: host
            ?: ip

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

    /**
     * Извлекает IP-адрес, используя host из OVPN или IP из CSV.
     * Если host — домен, пытается его разрешить в IP.
     */
    fun resolveIp(csvHost: String?, csvIp: String?): String? {
        if (csvHost == null) return csvIp
        if (isValidIp(csvHost)) return csvHost

        val resolved = getIpFromHost(csvHost)
        return resolved ?: csvIp
    }

    /** Декодирует и возвращает OVPN-конфигурацию из 14-й колонки. */
    fun parseCsvConfig(parts: List<String>): String? {
        return parts.getOrNull(14)?.let { decodeConfig(it) }
    }

    /** Извлекает имя сервера из 3-й колонки, если оно не пустое. */
    fun parseCsvName(parts: List<String>): String? {
        return parts.getOrNull(2)?.trim()?.takeIf { it.isNotBlank() }
    }

    /** Извлекает IP-адрес (или домен) из 2-й колонки. */
    fun parseCsvIp(parts: List<String>): String? {
        return parts.getOrNull(1)?.takeIf { it.isNotBlank() }
    }

    /** Извлекает порт из 12-й колонки, если он есть (необязательное поле). */
    fun parseCsvPort(parts: List<String>): Int? {
        return parts.getOrNull(11)?.toIntOrNull()
    }

    /** Извлекает ping из 4-й колонки, если он числовой. */
    fun parseCsvPing(parts: List<String>): Int? {
        return parts.getOrNull(3)?.toIntOrNull()
    }

    /** Извлекает код страны из 7-й колонки, в нижнем регистре. */
    fun parseCsvCountry(parts: List<String>): String? {
        return parts.getOrNull(6)?.takeIf { it.isNotBlank() }?.lowercase()
    }
}
