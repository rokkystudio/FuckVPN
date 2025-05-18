package fuck.system.vpn.serverlist

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

object ServerListStorage
{
    private const val PREF_NAME = "vpn_server_list"
    private const val KEY_SERVERS = "servers"

    fun saveAll(context: Context, servers: List<ServerListItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (server in servers) {
            val obj = JSONObject().apply {
                put("ip", server.ip)
                put("country", server.country)
                put("ping", server.ping)
                put("favorite", server.favorite)
                put("config", server.openVpnConfigBase64)
            }
            jsonArray.put(obj)
        }
        prefs.edit { putString(KEY_SERVERS, jsonArray.toString()) }
    }

    fun loadAll(context: Context): List<ServerListItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SERVERS, null) ?: return emptyList()
        val result = mutableListOf<ServerListItem>()

        val jsonArray = JSONArray(jsonStr)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                ServerListItem(
                    ip = obj.getString("ip"),
                    country = obj.getString("country"),
                    ping = obj.getInt("ping"),
                    openVpnConfigBase64 = obj.getString("config"),
                    favorite = obj.optBoolean("favorite", false)
                )
            )
        }
        return result
    }
}