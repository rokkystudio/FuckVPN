package fuck.system.vpn.serverlist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import fuck.system.vpn.countryfilter.CountryFilterFragment
import fuck.system.vpn.countryfilter.CountryFilterStorage

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ServerListFragment : Fragment(R.layout.fragment_server_list)
{
    private val assetCsv: String = "vpngate.csv"
    private val webCsv: String = "https://www.vpngate.net/api/iphone/"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServerAdapter
    private val vpnServers = mutableListOf<ServerListItem>()
    private var filteredServers = mutableListOf<ServerListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerServers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ServerAdapter(filteredServers)
        recyclerView.adapter = adapter

        val buttonCountryFilter: Button = view.findViewById(R.id.buttonCountryFilter)
        buttonCountryFilter.setOnClickListener {
            openCountryFilter()
        }

        val buttonRefresh: Button = view.findViewById(R.id.buttonDownloadServers)
        buttonRefresh.setOnClickListener {
            loadServersFromWeb()
        }

        val buttonEmptyServers: Button = view.findViewById(R.id.buttonEmptyServers)
        buttonEmptyServers.setOnClickListener {
            showEmptyServersDialog()
        }

        if (vpnServers.isEmpty()) {
            vpnServers.addAll(ServerListStorage.loadAll(requireContext()))
        }

        if (vpnServers.isEmpty()) {
            loadServersFromAssets(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()

        // Восстановить выбранную кнопку в нижнем меню
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navView.menu.findItem(R.id.nav_servers).isChecked = true

        context?.let { applyFiltersAndSort() }
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
        val handler = Handler(Looper.getMainLooper())

        // Флаг, что загрузка еще идет
        var isLoading = true

        // Показываем длинный тост что загрузка началась
        Toast.makeText(context, "Идёт загрузка списка серверов с сайта...", Toast.LENGTH_LONG).show()

        // Запускаем таймаут на 5 секунд, если загрузка не закончилась — покажем ошибку
        handler.postDelayed({
            if (isLoading)
            {
                Toast.makeText(context, "Не удалось загрузить данные (таймаут)", Toast.LENGTH_SHORT).show()
            }
        }, 5000)

        thread {
            try {
                Log.d("VPN", "Start loading servers from web...")

                val url = URL(webCsv)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                val responseCode = connection.responseCode
                Log.d("VPN", "Response code: $responseCode")

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code $responseCode")
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                parseCsv(reader)

                activity?.runOnUiThread {
                    isLoading = false
                    applyFiltersAndSort()
                    Toast.makeText(context, "Список серверов обновлён", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    isLoading = false
                    Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseCsv(reader: BufferedReader)
    {
        val existingMap = vpnServers.associateBy { it.ip }.toMutableMap()

        var line: String?
        var lineCount = 0
        while (reader.readLine().also { line = it } != null)
        {
            lineCount++
            if (lineCount <= 2 || line.isNullOrBlank()) continue

            val parts = line!!.split(",")
            if (parts.size < 15) continue

            val ip = parts[1]
            if (existingMap.containsKey(ip)) continue

            val country = parts[6].lowercase()
            val ping = parts[3].toIntOrNull() ?: 999
            val config = parts[14]

            val newServer = ServerListItem(ip = ip, country = country, ping = ping, favorite = false, openVpnConfigBase64 = config)
            existingMap[ip] = newServer
        }

        reader.close()

        vpnServers.clear()
        vpnServers.addAll(existingMap.values)
        ServerListStorage.saveAll(requireContext(), vpnServers)
    }

    private fun showEmptyServersDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистка списка серверов")
            .setMessage("Вы действительно хотите очистить список серверов?")
            .setPositiveButton("Да") { _, _ ->
                emptyServers()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun emptyServers() {
        vpnServers.clear()
        ServerListStorage.saveAll(requireContext(), vpnServers)
        applyFiltersAndSort()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyFiltersAndSort()
    {
        val countryFilters = CountryFilterStorage.loadAll(requireContext())

        filteredServers.clear()
        filteredServers.addAll(
            vpnServers.run {
                filter { server ->
                    val filter = countryFilters.find { it.country == server.country }
                    val enabled = filter == null || filter.enabled
                    enabled
                }.sortedWith(
                    compareByDescending<ServerListItem> { it.favorite }
                        .thenBy { it.ping }
                )
            }
        )

        adapter.notifyDataSetChanged()
    }

    private fun extractUniqueCountryCodes(): List<String> {
        return vpnServers.map { it.country }.distinct().sorted()
    }

    private fun openCountryFilter()
    {
        val uniqueCountryCodes = extractUniqueCountryCodes()

        val fragment = CountryFilterFragment()
        val bundle = Bundle().apply {
            putStringArrayList("country_codes", ArrayList(uniqueCountryCodes))
        }
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
