package fuck.system.vpn.servers.server

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

object ServerStorage
{
    private const val PREF_NAME = "vpn_server_list"
    private const val KEY_SERVERS = "servers"

    // Расширение для безопасного получения строки или null
    private fun JSONObject.optStringOrNull(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null

    // Расширение для безопасного получения Int или null
    private fun JSONObject.optIntOrNull(key: String): Int? =
        if (has(key) && !isNull(key)) getInt(key) else null

    fun saveAll(context: Context, servers: List<ServerItem>)
    {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        for (server in servers) {
            val obj = JSONObject().apply {
                put("name", server.name)
                put("ip", server.ip)
                put("port", server.port)
                put("country", server.country)
                put("ping", server.ping ?: JSONObject.NULL)
                put("favorite", server.favorite)
                put("ovpn", server.ovpn)
            }
            jsonArray.put(obj)
        }
        prefs.edit { putString(KEY_SERVERS, jsonArray.toString()) }
    }

    fun loadAll(context: Context): List<ServerItem>
    {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SERVERS, null) ?: return emptyList()
        val result = mutableListOf<ServerItem>()
        val jsonArray = JSONArray(jsonStr)

        for (i in 0 until jsonArray.length())
        {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                ServerItem(
                    name = obj.optStringOrNull("name") ?: "",
                    ovpn = obj.optStringOrNull("ovpn") ?: "",
                    favorite = obj.optBoolean("favorite", false),
                    ip = obj.optStringOrNull("ip"),
                    port = obj.optIntOrNull("port"),
                    country = obj.optStringOrNull("country"),
                    ping = obj.optIntOrNull("ping")
                )
            )
        }
        return result
    }
}
