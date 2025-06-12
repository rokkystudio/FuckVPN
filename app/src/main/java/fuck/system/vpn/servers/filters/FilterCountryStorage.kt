package fuck.system.vpn.servers.filters

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit
import fuck.system.vpn.servers.server.ServerGeo

/**
 * Хранилище состояния фильтрации стран.
 *
 * Отвечает за сохранение/загрузку списка стран с включённой или отключённой видимостью
 * в общем списке серверов. Использует SharedPreferences.
 */
object FilterCountryStorage
{
    /** Имя SharedPreferences-файла */
    const val PREF_NAME = "country_filters_prefs"

    /** Ключ под которым хранится JSON со списком фильтров */
    const val KEY_FILTER = "country_filters"

    /**
     * Сохраняет весь список фильтров стран в SharedPreferences.
     *
     * @param context Контекст приложения
     * @param filterList Список фильтров (страна + вкл/выкл)
     */
    fun saveAll(context: Context, filterList: List<FilterCountryItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONArray()
        filterList.forEach {
            val obj = JSONObject().apply {
                put("country", it.country)
                put("enabled", it.enabled)
            }
            json.put(obj)
        }
        prefs.edit(commit = true) { // <-- гарантированная запись
            putString(KEY_FILTER, json.toString())
        }
    }

    /**
     * Сохраняет одиночный фильтр в SharedPreferences.
     *
     * Используется в случаях, когда обновляется только один элемент.
     *
     * @param context Контекст приложения
     * @param item Фильтр для одной страны
     */
    fun saveItem(context: Context, item: FilterCountryItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONArray()
        val obj = JSONObject().apply {
            put("country", item.country)
            put("enabled", item.enabled)
        }
        json.put(obj)
        prefs.edit(commit = true) { // <-- то же самое
            putString(KEY_FILTER, json.toString())
        }
    }

    /**
     * Загружает все фильтры стран из SharedPreferences.
     *
     * Если данных нет или они повреждены, возвращает список со всеми странами включёнными.
     *
     * @param context Контекст приложения
     * @return Список фильтров
     */
    fun loadAll(context: Context): List<FilterCountryItem>
    {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_FILTER, null)

        val result = mutableListOf<FilterCountryItem>()

        if (jsonStr.isNullOrEmpty()) {
            return initDefault(context)
        }

        try {
            val json = JSONArray(jsonStr)
            for (i in 0 until json.length())
            {
                val obj = json.getJSONObject(i)
                val country = obj.getString("country")
                val enabled = obj.optBoolean("enabled", true)

                result.add( FilterCountryItem(country = country, enabled = enabled) )
            }
        } catch (e: Exception) {
            // Если ошибка — делаем инициализацию по умолчанию
            return initDefault(context)
        }

        return result
    }

    /**
     * Инициализирует фильтры со всеми странами включёнными.
     *
     * Также сохраняет их в SharedPreferences.
     *
     * @param context Контекст приложения
     * @return Список фильтров
     */
    private fun initDefault(context: Context): List<FilterCountryItem>
    {
        val filterList = mutableListOf<FilterCountryItem>()

        for ((country) in ServerGeo.countryNames) {
            filterList.add(
                FilterCountryItem(country = country, enabled = true)
            )
        }

        saveAll(context, filterList)
        return filterList
    }

    /**
     * Регистрирует слушателя изменений SharedPreferences по фильтрам.
     *
     * Используется для автообновления UI при изменении фильтров.
     *
     * @param context Контекст
     * @param listener Слушатель
     */
    fun observe(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Удаляет слушателя изменений SharedPreferences.
     *
     * @param context Контекст
     * @param listener Слушатель
     */
    fun removeObserver(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}