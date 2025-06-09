package fuck.system.vpn.servers.filters

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit
import fuck.system.vpn.servers.server.ServerGeo

object FilterCountryStorage
{
    const val PREF_NAME = "country_filters_prefs"
    const val KEY_FILTER = "country_filters"

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
        prefs.edit { putString(KEY_FILTER, json.toString()) }
    }

    fun saveItem(context: Context, item: FilterCountryItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONArray()
        val obj = JSONObject().apply {
            put("country", item.country)
            put("enabled", item.enabled)
        }
        json.put(obj)
        prefs.edit { putString(KEY_FILTER, json.toString()) }
    }

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

    fun observe(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeObserver(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}