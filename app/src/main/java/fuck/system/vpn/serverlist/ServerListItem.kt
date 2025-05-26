package fuck.system.vpn.serverlist;

data class ServerListItem
(
    val name: String? = null,
    val ip: String,
    val port: Int,

    val country: String? = null,
    var ping: Int? = null,

    var favorite: Boolean = false,

    val username: String? = null,
    val password: String? = null,
    val protocol: String? = null,
    val psk: String? = null,
    val ovpn: String? = null
)