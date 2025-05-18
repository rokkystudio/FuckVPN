package fuck.system.vpn.countryfilter

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit
import fuck.system.vpn.serverlist.FlagMap

object CountryFilterStorage
{
    private const val PREF_NAME = "country_filter_prefs"
    private const val KEY_FILTER = "country_filter_map"

    fun saveAll(context: Context, filterList: List<CountryFilterItem>) {
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

    fun saveItem(context: Context, item: CountryFilterItem) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONArray()
        val obj = JSONObject().apply {
            put("country", item.country)
            put("enabled", item.enabled)
        }
        json.put(obj)
        prefs.edit { putString(KEY_FILTER, json.toString()) }
    }

    fun loadAll(context: Context): List<CountryFilterItem>
    {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_FILTER, null)

        val result = mutableListOf<CountryFilterItem>()

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

                result.add( CountryFilterItem(country = country, enabled = enabled) )
            }
        } catch (e: Exception) {
            // Если ошибка — делаем инициализацию по умолчанию
            return initDefault(context)
        }

        return result
    }


    fun initDefault(context: Context): List<CountryFilterItem>
    {
        val filterList = mutableListOf<CountryFilterItem>()

        for ((country) in FlagMap.countryNames) {
            filterList.add(
                CountryFilterItem(country = country, enabled = true)
            )
        }

        saveAll(context, filterList)
        return filterList
    }
}