package fuck.system.vpn.status

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import fuck.system.vpn.servers.server.ServerItem

/**
 * Хранилище последнего подключённого сервера и состояния автоподключения.
 *
 * Используется для восстановления последнего подключения и запоминания
 * флага "автоматического подключения" при старте приложения.
 */
object LastServerStorage
{
    /** Имя файла SharedPreferences */
    private const val PREF_NAME = "vpn_server_last"

    /** Ключ для сохранения последнего выбранного сервера */
    private const val KEY_LAST_SERVER = "last_server_item"

    /** Ключ для флага автоматического подключения */
    private const val KEY_AUTO_CONNECT = "auto_connect"

    /** Ленивая инициализация Gson */
    private val gson by lazy { Gson() }

    /**
     * Сохраняет последний использованный сервер.
     *
     * @param context Контекст приложения
     * @param server Сервер, который нужно запомнить
     */
    fun save(context: Context, server: ServerItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit(commit = true) { // Гарантируем, что запись произойдёт сразу
            putString(KEY_LAST_SERVER, gson.toJson(server))
        }
    }

    /**
     * Загружает последний использованный сервер.
     *
     * @param context Контекст приложения
     * @return Сохранённый сервер или null, если нет данных
     */
    fun load(context: Context): ServerItem? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LAST_SERVER, null) ?: return null
        return gson.fromJson(json, ServerItem::class.java)
    }

    /**
     * Очищает сохранённый сервер и флаг автоподключения.
     *
     * @param context Контекст приложения
     */
    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_LAST_SERVER)
            remove(KEY_AUTO_CONNECT)
        }
    }

    /**
     * Устанавливает флаг автоподключения.
     *
     * @param context Контекст приложения
     * @param enabled true — включить автоподключение, false — отключить
     */
    fun setAutoConnect(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_AUTO_CONNECT, enabled) }
    }

    /**
     * Получает текущее состояние флага автоподключения.
     *
     * @param context Контекст приложения
     * @return true — включено, false — отключено
     */
    fun getAutoConnect(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_CONNECT, false)
    }
}
