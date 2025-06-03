package fuck.system.vpn.servers.server

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ServerStorage {
    private const val PREF_NAME = "vpn_server_list"
    private const val KEY_SERVERS = "servers"

    fun saveAll(context: Context, servers: List<ServerItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(servers)
        prefs.edit().putString(KEY_SERVERS, json).apply()
    }

    fun loadAll(context: Context): List<ServerItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SERVERS, null) ?: return emptyList()
        val type = object : TypeToken<List<ServerItem>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}