package fuck.system.vpn.serverlist;

data class VpnServer(
    val ip: String,
    val country: String,
    val countryCode: String,
    val ping: Int,
    val openVpnConfigBase64: String
)