package fuck.system.vpn.serverlist;

data class ServerListItem(
    val ip: String,
    val country: String,
    val ping: Int,
    val favorite: Boolean,
    val openVpnConfigBase64: String
)