package fuck.system.vpn.serverlist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ServerListFragment : Fragment()
{
    private val assetCsv: String = "vpngate.csv"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServerAdapter
    private val vpnServers = mutableListOf<VpnServer>()
    private var filteredServers = mutableListOf<VpnServer>()
    private val selectedCountries = mutableSetOf<String>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_server_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("vpn_settings", Context.MODE_PRIVATE)
        selectedCountries.addAll(sharedPreferences.getStringSet("selected_countries", emptySet()) ?: emptySet())

        recyclerView = view.findViewById(R.id.recyclerServers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ServerAdapter(filteredServers)
        recyclerView.adapter = adapter

        val buttonCountryFilter: Button = view.findViewById(R.id.buttonCountryFilter)
        buttonCountryFilter.setOnClickListener {
            showCountryDialog()
        }

        val buttonRefresh: Button = view.findViewById(R.id.buttonDownloadServers)
        buttonRefresh.setOnClickListener {
            loadServersFromWeb()
        }

        if (vpnServers.isEmpty()) {
            loadServersFromAssets(requireContext())
        }
    }

    private fun loadServersFromAssets(context: Context)
    {
        try {
            context.assets.open(assetCsv).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    parseCsv(reader)
                    applyFiltersAndSort()
                }
            }
        } catch (e: IOException) {
            Log.e("VPN", "Ошибка чтения $assetCsv из assets", e)
        }
    }

    private fun loadServersFromWeb()
    {
        thread {
            try {
                val url = URL("http://www.vpngate.net/api/iphone/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                parseCsv(reader)

                activity?.runOnUiThread {
                    applyFiltersAndSort()
                    Toast.makeText(context, "Список серверов обновлён", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseCsv(reader: BufferedReader)
    {
        var line: String?
        var lineCount = 0
        while (reader.readLine().also { line = it } != null) {
            lineCount++
            if (lineCount <= 2 || line.isNullOrBlank()) continue

            val parts = line!!.split(",")
            if (parts.size < 15) continue

            val ip = parts[1]
            if (vpnServers.any { it.ip == ip }) continue

            val country = parts[6]
            val countryCode = parts[7].lowercase()
            val ping = parts[3].toIntOrNull() ?: 999
            val config = parts[14]

            vpnServers.add(VpnServer(ip = ip, country = country, countryCode = countryCode, ping = ping, openVpnConfigBase64 = config))
        }
        reader.close()
    }

    private fun applyFiltersAndSort() {
        filteredServers.clear()
        filteredServers.addAll(
            vpnServers.filter { selectedCountries.isEmpty() || it.country in selectedCountries }
                .sortedBy { it.ping }
        )
        adapter.notifyDataSetChanged()
    }

    private fun showCountryDialog() {
        val allCountries = vpnServers.map { it.country }.toSet().union(selectedCountries)
        val countryArray = allCountries.sorted().toTypedArray()
        val checkedItems = BooleanArray(countryArray.size) { selectedCountries.contains(countryArray[it]) }

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите страны")
            .setMultiChoiceItems(countryArray, checkedItems) { _, which, isChecked ->
                if (isChecked) selectedCountries.add(countryArray[which])
                else selectedCountries.remove(countryArray[which])
            }
            .setPositiveButton("OK") { _, _ ->
                sharedPreferences.edit().putStringSet("selected_countries", selectedCountries).apply()
                applyFiltersAndSort()
            }
            .setNeutralButton("Выбрать все") { _, _ ->
                selectedCountries.clear()
                selectedCountries.addAll(allCountries)
                sharedPreferences.edit().putStringSet("selected_countries", selectedCountries).apply()
                applyFiltersAndSort()
            }
            .setNegativeButton("Очистить") { _, _ ->
                selectedCountries.clear()
                sharedPreferences.edit().putStringSet("selected_countries", selectedCountries).apply()
                applyFiltersAndSort()
            }
            .show()
    }
}
