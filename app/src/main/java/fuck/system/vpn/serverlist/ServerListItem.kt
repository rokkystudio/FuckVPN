package fuck.system.vpn.serverlist;

data class ServerListItem
(
    val ip: String,
    val port: Int,
    var favorite: Boolean,

    val country: String,
    var ping: Int,

    val username: String,
    val password: String,
    val protocol: String,

    val openVpnConfigBase64: String? = null
)