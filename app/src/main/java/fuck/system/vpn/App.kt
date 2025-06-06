package fuck.system.vpn

import android.app.Application
import android.content.Context
import fuck.system.vpn.servers.server.ServerGeo

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServerGeo.init(this)
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
            private set
    }
}