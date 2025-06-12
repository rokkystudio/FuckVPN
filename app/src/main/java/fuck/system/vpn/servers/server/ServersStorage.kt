package fuck.system.vpn.servers.server

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

/**
 * Хранилище VPN-серверов (основной список).
 *
 * Использует SharedPreferences для сохранения/загрузки списка серверов
 * в формате JSON. Также позволяет подписываться на изменения и реагировать на них
 * в фрагментах или активностях.
 */
object ServersStorage
{
    /** Имя SharedPreferences-файла */
    const val PREF_NAME = "vpn_server_list"

    /** Ключ под которым хранится JSON со списком серверов */
    const val KEY_SERVERS = "servers"

    /**
     * Сохраняет список серверов в SharedPreferences.
     *
     * Используется при обновлении с сервера, добавлении/удалении вручную и т.п.
     * Запись производится с `commit = true`, чтобы гарантировать срабатывание
     * OnSharedPreferenceChangeListener сразу после изменения.
     *
     * @param context Контекст приложения
     * @param servers Список серверов для сохранения
     */
    fun save(context: Context, servers: List<ServerItem>)
    {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val dataJson = gson.toJson(servers)

        val wrapper = org.json.JSONObject().apply {
            put("data", org.json.JSONArray(dataJson))
            put("updatedAt", System.currentTimeMillis())
        }

        prefs.edit(commit = true) {
            putString(KEY_SERVERS, wrapper.toString())
        }
    }

    /**
     * Загружает список серверов из SharedPreferences.
     *
     * Если данные отсутствуют или некорректны — возвращает пустой список.
     *
     * @param context Контекст приложения
     * @return Список серверов
     */
    fun load(context: Context): MutableList<ServerItem>
    {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SERVERS, null) ?: return mutableListOf()

        return try {
            val wrapper = org.json.JSONObject(raw)
            val dataArray = wrapper.getJSONArray("data").toString()
            val type = object : TypeToken<List<ServerItem>>() {}.type
            Gson().fromJson<List<ServerItem>>(dataArray, type)?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    /**
     * Регистрирует слушателя изменений SharedPreferences.
     *
     * Вызывается при любом изменении ключа [KEY_SERVERS].
     * Используется в фрагментах, чтобы автоматически обновлять UI при изменении списка серверов.
     *
     * @param context Контекст
     * @param listener Слушатель (например, `SharedPreferences.OnSharedPreferenceChangeListener`)
     */
    fun observe(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Удаляет ранее зарегистрированного слушателя изменений.
     *
     * @param context Контекст
     * @param listener Слушатель, который больше не нужен
     */
    fun removeObserver(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}