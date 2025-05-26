package fuck.system.vpn.serverlist

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import fuck.system.vpn.R

object ServerListStorage
{
    private const val PREF_NAME = "vpn_server_list"
    private const val KEY_SERVERS = "servers"

    // Расширение для безопасного получения строки или null
    private fun JSONObject.optStringOrNull(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null

    // Расширение для безопасного получения Int или null
    private fun JSONObject.optIntOrNull(key: String): Int? =
        if (has(key) && !isNull(key)) getInt(key) else null

    /**
     * Сохраняет список серверов в SharedPreferences в виде JSON.
     */
    fun saveAll(context: Context, servers: List<ServerListItem>)
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
                put("username", server.username)
                put("password", server.password)
                put("protocol", server.protocol)
                put("psk", server.psk)
                put("ovpn", server.ovpn)
            }
            jsonArray.put(obj)
        }
        prefs.edit { putString(KEY_SERVERS, jsonArray.toString()) }
    }

    /**
     * Загружает список серверов из SharedPreferences и парсит их в объекты ServerListItem.
     */
    fun loadAll(context: Context): List<ServerListItem>
    {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SERVERS, null) ?: return emptyList()
        val result = mutableListOf<ServerListItem>()

        // Получение значения порта по умолчанию из ресурсов
        val defaultPort = context.resources.getInteger(R.integer.default_vpn_port)

        val jsonArray = JSONArray(jsonStr)
        for (i in 0 until jsonArray.length())
        {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                ServerListItem(
                    name = obj.optStringOrNull("name"),
                    ip = obj.getString("ip"),
                    port = obj.optInt("port", defaultPort),
                    country = obj.optStringOrNull("country"),
                    ping = obj.optIntOrNull("ping"),
                    favorite = obj.optBoolean("favorite", false),
                    username = obj.optStringOrNull("username"),
                    password = obj.optStringOrNull("password"),
                    protocol = obj.optStringOrNull("protocol"),
                    psk = obj.optStringOrNull("psk"),
                    ovpn = obj.optStringOrNull("ovpn")
                )
            )
        }
        return result
    }
}
