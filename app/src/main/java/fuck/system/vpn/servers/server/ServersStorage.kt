package fuck.system.vpn.servers.server

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object ServersStorage
{
    const val PREF_NAME = "vpn_server_list"
    const val KEY_SERVERS = "servers"

    fun save(context: Context, servers: List<ServerItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(servers)
        prefs.edit { putString(KEY_SERVERS, json) }
    }

    fun load(context: Context): MutableList<ServerItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SERVERS, null) ?: return mutableListOf()
        val type = object : TypeToken<List<ServerItem>>() {}.type
        return Gson().fromJson<List<ServerItem>>(json, type)?.toMutableList() ?: mutableListOf()
    }

    fun observe(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeObserver(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}