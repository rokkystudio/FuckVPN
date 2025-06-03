package fuck.system.vpn.parser

data class OvpnInfo(
    val host: String?,
    val port: Int?,
    val proto: String?,
    val country: String? = null // Если удастся найти по ключу или комментариям
)

class OvpnParser
{
    companion object {
        /**
         * Простейший парсер: ищет remote, proto, port, country (если есть комментарий #COUNTRY=RU)
         */
        fun parse(ovpn: String): OvpnInfo {
            var host: String? = null
            var port: Int? = null
            var proto: String? = null
            var country: String? = null

            ovpn.lines().forEach { line ->
                val trimmed = line.trim()

                // remote host port
                if (trimmed.startsWith("remote ")) {
                    val parts = trimmed.split("\\s+".toRegex())
                    if (parts.size >= 3) {
                        host = parts[1]
                        port = parts[2].toIntOrNull()
                    } else if (parts.size == 2) {
                        host = parts[1]
                    }
                }
                // proto udp/tcp
                if (trimmed.startsWith("proto ")) {
                    proto = trimmed.substringAfter("proto").trim()
                }
                // port (опционально)
                if (trimmed.startsWith("port ")) {
                    port = trimmed.substringAfter("port").trim().toIntOrNull()
                }
                // country (например, #COUNTRY=RU)
                if (trimmed.startsWith("#") && trimmed.contains("COUNTRY=", ignoreCase = true)) {
                    country = trimmed.substringAfter("COUNTRY=").trim()
                }
            }

            return OvpnInfo(host, port, proto, country)
        }
    }
}
