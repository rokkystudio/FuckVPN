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
            System.loadLibrary("libovpncli")
            Log.d("App", "✅ All native libs loaded")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("App", "❌ Failed to load native libs", e)
        }

        ServerGeo.init(this)
    }
}