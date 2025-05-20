package fuck.system.vpn.serverlist;

data class ServerListItem(
    val ip: String,
    val country: String,
    var ping: Int,
    var favorite: Boolean,
    val openVpnConfigBase64: String
)