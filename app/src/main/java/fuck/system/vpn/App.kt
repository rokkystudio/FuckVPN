package fuck.system.vpn

import android.app.Application
import android.content.Context
import android.util.Log
import fuck.system.vpn.servers.server.ServerGeo

class App : Application()
{
    companion object {
        fun context(): Context = instance
        private lateinit var instance: Application
    }

    override fun onCreate()
    {
        super.onCreate()
        instance = this

        try {
            System.loadLibrary("ovpnexec")        // исполняемый бинарник
            System.loadLibrary("openvpn")         // основной VPN-движок
            System.loadLibrary("ovpn3")           // если используете OpenVPN3
            System.loadLibrary("osslutil")        // openssl утилиты
            System.loadLibrary("osslspeedtest")   // если используется
            System.loadLibrary("ovpnutil")        // это уже было
            Log.d("App", "✅ All native libs loaded")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("App", "❌ Failed to load native libs", e)
        }

        ServerGeo.init(this)
    }
}