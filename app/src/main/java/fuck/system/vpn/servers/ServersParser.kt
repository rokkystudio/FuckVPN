package fuck.system.vpn.servers

import android.util.Base64
import fuck.system.vpn.servers.server.ServerItem
import java.io.BufferedReader

object ServersParser
{
    /**
     * Декодирует base64-строку конфигурации. Если не удалось — возвращает исходную строку.
     */
    fun decodeConfig(input: String): String {
        return try {
            String(Base64.decode(input, Base64.DEFAULT))
        } catch (e: Exception) {
            input
        }
    }

    /**
     * Парсит порт из OpenVPN-конфига (ovpn).
     */
    fun extractPortFromOvpn(ovpn: String): Int? {
        val regex = Regex("""remote\s+[\w.-]+ (\d+)""")
        val match = regex.find(ovpn)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    /**
     * Парсит CSV-файл VPNGate и возвращает список ServerItem.
     */
    fun parseCsv(reader: BufferedReader, oldServers: List<ServerItem> = emptyList()): List<ServerItem>
    {
        val existingMap = oldServers.associateBy { it.ip }.toMutableMap()
        var line: String?
        var lineCount = 0

        while (reader.readLine().also { line = it } != null)
        {
            lineCount++
            if (lineCount <= 2 || line.isNullOrBlank()) continue

            val parts = line!!.split(",")
            if (parts.size < 15) continue

            val ip = parts[1]
            if (existingMap.containsKey(ip)) continue

            val country = parts[6].lowercase()
            val ping = parts[3].toIntOrNull()
            val hostName = parts[2].trim()
            val ovpnRaw = parts[14]
            val ovpn = decodeConfig(ovpnRaw)
            val port = extractPortFromOvpn(ovpn)
            val name = if (hostName.isNotEmpty()) hostName else ip

            val newServer = ServerItem(name = name, ip = ip, port = port,
                country = country, ping = ping, favorite = false, ovpn = ovpn)
            existingMap[ip] = newServer
        }

        reader.close()
        return existingMap.values.toList()
    }
}
