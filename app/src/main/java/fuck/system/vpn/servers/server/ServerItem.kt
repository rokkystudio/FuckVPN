package fuck.system.vpn.servers.server;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerItem(
    val name: String,
    val ovpn: String,
    var favorite: Boolean = false,
    val ip: String? = null,
    val port: Int? = null,
    val country: String? = null,
    var ping: Int? = null
) : Parcelable