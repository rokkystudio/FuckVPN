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
    fun decodeOvpn(input: String): String {
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
    fun getOvpnHost(ovpn: String): String?
    {
        val lines = ovpn.lines()
        for (line in lines) {
            val trimmed = line.trimStart()

            // Пропускаем строку, если она полностью комментарий
            if (trimmed.startsWith("#")) continue

            // Отрезаем комментарий справа, если есть
            val code = trimmed.substringBefore("#").trim()

            // Ищем remote <host>
            val match = Regex("""^remote\s+([^\s]+)""").find(code)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    /**
     * Извлекает порт из строки remote в ovpn-конфиге.
     * Пример строки: "remote vpn.example.com 1194"
     */
    fun getOvpnPort(ovpn: String): Int?
    {
        val lines = ovpn.lines()
        for (line in lines) {
            val trimmed = line.trimStart()

            // Пропускаем строку, если это комментарий
            if (trimmed.startsWith("#")) continue

            // Отрезаем комментарий справа
            val code = trimmed.substringBefore("#").trim()

            // Ищем строку вида: remote <host> <port>
            val match = Regex("""^remote\s+[^\s]+\s+(\d+)""").find(code)
            if (match != null) return match.groupValues[1].toIntOrNull()
        }
        return null
    }

    /**
     * Извлекает протокол из ovpn-конфига ("udp" или "tcp").
     */
    fun getOvpnProto(ovpn: String): String?
    {
        val lines = ovpn.lines()
        for (line in lines) {
            val trimmed = line.trimStart()

            // Пропускаем комментарии
            if (trimmed.startsWith("#")) continue

            // Обрезаем комментарии справа
            val code = trimmed.substringBefore("#").trim()

            // Ищем протокол: proto udp или proto tcp
            val match = Regex("""^proto\s+(udp|tcp)\b""").find(code)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    /**
     * Извлекает страну из строки #COUNTRY=XX (без учёта регистра).
     */
    fun getOvpnCountry(ovpn: String): String?
    {
        val lines = ovpn.lines()
        for (line in lines) {
            val trimmed = line.trimStart()
            if (!trimmed.startsWith("#")) continue

            val match = Regex("""(?i)#.*\bCOUNTRY\s*=\s*([a-z]{2})""").find(trimmed)
            if (match != null) {
                return match.groupValues[1].lowercase()
            }
        }
        return null
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
    fun resolveHost(host: String): String?
    {
        if (isValidIp(host)) return host

        return try {
            InetAddress.getByName(host).hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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

        val base64 = parseCsvConfig(parts) ?: return null
        val ovpn = decodeOvpn(base64).trim()

        val host = getOvpnHost(ovpn)

        // Пробуем получить IP-адрес
        var ip: String? = parseCsvIp(parts)

        if (host != null) {
            ip = resolveHost(host)
        }

        // Если нет даже host и ip — это совсем сломанная строка
        if (host == null && ip == null) return null

        val port = getOvpnPort(ovpn) ?: parseCsvPort(parts)
        val proto = getOvpnProto(ovpn)
        val ping = parseCsvPing(parts)

        var country: String? = getOvpnCountry(ovpn)

        if (country == null) {
            country = parseCsvCountry(parts)
        }

        if (country == null && ip != null) {
            country = getCountryByIp(ip)
        }

        val name = parseCsvName(parts)
            ?: host
            ?: ip
            ?: "Unknown"

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

    /** Декодирует и возвращает OVPN-конфигурацию из 14-й колонки. */
    fun parseCsvConfig(parts: List<String>): String? {
        return parts.getOrNull(14)
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
