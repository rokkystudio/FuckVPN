package fuck.system.vpn.status

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import fuck.system.vpn.servers.server.ServerItem

object LastServerStorage
{
    private const val PREF_NAME = "vpn_server_last"
    private const val KEY_LAST_SERVER = "last_server_item"
    private const val KEY_AUTO_CONNECT = "auto_connect"
    private val gson by lazy { Gson() }

    fun save(context: Context, server: ServerItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LAST_SERVER, gson.toJson(server)) }
    }

    fun load(context: Context): ServerItem? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LAST_SERVER, null) ?: return null
        return gson.fromJson(json, ServerItem::class.java)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_LAST_SERVER)
            remove(KEY_AUTO_CONNECT)
        }
    }

    fun setAutoConnect(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_AUTO_CONNECT, enabled) }
    }

    fun getAutoConnect(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_CONNECT, false)
    }
}
