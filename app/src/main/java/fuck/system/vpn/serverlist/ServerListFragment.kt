package fuck.system.vpn.serverlist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import fuck.system.vpn.serverlist.addserver.AddServerDialog
import fuck.system.vpn.serverlist.countryfilter.CountryFilterDialog
import fuck.system.vpn.serverlist.countryfilter.CountryFilterStorage
import fuck.system.vpn.serverlist.getservers.GetServersDialog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ServerListFragment : Fragment(R.layout.fragment_server_list)
{
    private val assetCsv: String = "vpngate.csv"

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

        setupButtons(view)

        if (vpnServers.isEmpty()) {
            vpnServers.addAll(ServerListStorage.loadAll(requireContext()))
        }

        if (vpnServers.isEmpty()) {
            loadServersFromAssets(requireContext())
        }

        // Добавляем слушатель результата из CountryFilterDialog
        parentFragmentManager.setFragmentResultListener("filter_changed", this) { _, _ ->
            updateServersWithPing()
        }
    }

    override fun onResume() {
        super.onResume()

        // Восстановить выбранную кнопку в нижнем меню
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navView.menu.findItem(R.id.nav_servers).isChecked = true

        context?.let { updateServers() }
    }

    private fun setupButtons(view: View)
    {
        view.findViewById<Button>(
            R.id.ServersButtonFilter)
            .setOnClickListener {
                openCountryFilterDialog()
            }

        view.findViewById<Button>(
            R.id.ServersButtonGetServers)
            .setOnClickListener {
                openGetServersDialog()
            }

        view.findViewById<Button>(
            R.id.ServersButtonAddServer)
            .setOnClickListener {
                openAddServerDialog()
            }

        view.findViewById<Button>(
            R.id.ServersButtonEmptyServers)
            .setOnClickListener {
                openEmptyServersDialog()
            }
    }

    private fun loadServersFromAssets(context: Context) {
        try {
            context.assets.open(assetCsv).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    parseCsv(reader)
                    updateServersWithPing()
                }
            }
        } catch (e: IOException) {
            Log.e("VPN", "Ошибка чтения $assetCsv из assets", e)
        }
    }

    private fun openAddServerDialog() {
        AddServerDialog.newInstance { newServer ->
            vpnServers.add(newServer)
            updateServersWithPing()
        }.show(parentFragmentManager, AddServerDialog.TAG)
    }

    private fun openGetServersDialog() {
        val dialog = GetServersDialog.newInstance(vpnServers) { servers ->
            applyWebServers(servers)
        }
        dialog.show(parentFragmentManager, GetServersDialog.TAG)
    }

    private fun parseCsv(reader: BufferedReader) {
        val existingMap = vpnServers.associateBy { it.ip }.toMutableMap()

        var line: String?
        var lineCount = 0
        while (reader.readLine().also { line = it } != null) {
            lineCount++
            if (lineCount <= 2 || line.isNullOrBlank()) continue

            val parts = line!!.split(",")
            if (parts.size < 15) continue

            val ip = parts[1]
            if (existingMap.containsKey(ip)) continue

            val country = parts[6].lowercase()
            val ping = parts[3].toIntOrNull() ?: 999
            val config = parts[14]

            val newServer =
                ServerListItem(ip = ip, country = country, ping = ping, favorite = false, openVpnConfigBase64 = config)
            existingMap[ip] = newServer
        }

        reader.close()

        vpnServers.clear()
        vpnServers.addAll(existingMap.values)
        ServerListStorage.saveAll(requireContext(), vpnServers)
    }

    private fun openEmptyServersDialog() {
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
        updateServers()
    }

    private fun applyWebServers(servers: List<ServerListItem>) {
        val favorites = vpnServers.filter { it.favorite }.associateBy { it.ip }

        vpnServers.clear()
        vpnServers.addAll(
            servers.map {
                if (favorites.containsKey(it.ip)) it.copy(favorite = true) else it
            }
        )

        ServerListStorage.saveAll(requireContext(), vpnServers)
        Toast.makeText(requireContext(), "Список серверов обновлён", Toast.LENGTH_SHORT).show()

        updateServersWithPing()
    }

    private fun updateServersWithPing() {
        PingServersDialog(requireContext(), vpnServers) {
            updateServers()
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateServers() {
        val countryFilters = CountryFilterStorage.loadAll(requireContext())

        filteredServers.clear()
        filteredServers.addAll(
            vpnServers.filter { server ->
                val filter = countryFilters.find { it.country == server.country }
                filter == null || filter.enabled
            }
        )

        filteredServers.sortWith(
            compareByDescending<ServerListItem> { it.favorite }
                .thenBy { it.ping }
        )

        adapter.notifyDataSetChanged()
    }

    private fun extractUniqueCountryCodes(): List<String> {
        return vpnServers.map { it.country }.distinct().sorted()
    }

    private fun openCountryFilterDialog() {
        val uniqueCountryCodes = extractUniqueCountryCodes()
        val dialog = CountryFilterDialog.newInstance(uniqueCountryCodes)
        dialog.show(parentFragmentManager, CountryFilterDialog.TAG)
    }
}
