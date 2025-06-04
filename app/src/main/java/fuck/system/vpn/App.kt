package fuck.system.vpn

import android.app.Application
import fuck.system.vpn.servers.server.ServerGeo

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServerGeo.init(this)
    }
}