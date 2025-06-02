package fuck.system.vpn.servers.server;

data class ServerItem(
    val name: String,                // Название сервера или хост
    val ovpn: String,                // OpenVPN-конфиг (расшифрованный)
    val favorite: Boolean = false,   // Избранный сервер
    val ip: String? = null,          // Заполняется позже из ovpn файла
    val port: Int? = null,           // Заполняется позже из ovpn файла
    val country: String? = null,     // Короткое имя страны
    var ping: Int? = null            // Выполняется после добавления серверов
)