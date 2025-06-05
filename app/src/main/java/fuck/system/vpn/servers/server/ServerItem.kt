package fuck.system.vpn.servers.server;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerItem(
    var name: String,
    var ovpn: String,
    var favorite: Boolean = false,
    var ip: String? = null,
    var port: Int? = null,
    var country: String? = null,
    var ping: Int? = null,
    var proto: String? = null
) : Parcelable