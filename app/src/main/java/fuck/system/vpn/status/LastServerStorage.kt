package fuck.system.vpn.status

import android.content.Context
import com.google.gson.Gson
import fuck.system.vpn.servers.server.ServerItem

object LastServerStorage
{
    private const val PREF_NAME = "vpn_server_last"
    private const val KEY_LAST_SERVER = "last_server_item"
    private val gson by lazy { Gson() }

    fun save(context: Context, server: ServerItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LAST_SERVER, gson.toJson(server)).apply()
    }

    fun load(context: Context): ServerItem? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LAST_SERVER, null) ?: return null
        return gson.fromJson(json, ServerItem::class.java)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_LAST_SERVER).apply()
    }
}