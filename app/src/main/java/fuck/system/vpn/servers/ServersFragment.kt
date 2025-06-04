package fuck.system.vpn.servers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import fuck.system.vpn.parser.ServersParser
import fuck.system.vpn.servers.dialogs.AddServerDialog
import fuck.system.vpn.servers.filters.CountryFilterDialog
import fuck.system.vpn.servers.filters.CountryFilterStorage
import fuck.system.vpn.servers.dialogs.GetServersDialog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import fuck.system.vpn.servers.dialogs.PingServersDialog
import fuck.system.vpn.servers.dialogs.MenuServerDialog
import fuck.system.vpn.servers.dialogs.pingServers
import fuck.system.vpn.servers.server.ServerAdapter
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServerStorage
import fuck.system.vpn.status.LastServerStorage

class ServersFragment : Fragment(R.layout.fragment_servers)
{
    private val githubCsv = "https://raw.githubusercontent.com/rokkystudio/VPN/master/app/src/main/assets/vpngate.csv"
    private val assetCsv: String = "vpngate.csv"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServerAdapter
    private val vpnServers = mutableListOf<ServerItem>()
    private var filteredServers = mutableListOf<ServerItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerServers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ServerAdapter(filteredServers, object : ServerAdapter.OnServerClickListener {
            override fun onServerClick(isFavorite: Boolean, position: Int) {
                MenuServerDialog.newInstance(isFavorite, position)
                    .show(parentFragmentManager, MenuServerDialog.TAG)
            }
        })
        recyclerView.adapter = adapter

        parentFragmentManager.setFragmentResultListener(
            CountryFilterDialog.KEY, this) { _, _ ->
            updateServersWithPing()
        }

        parentFragmentManager.setFragmentResultListener(
            MenuServerDialog.RESULT_KEY, this) { _, bundle ->
            onServerMenuAction(bundle)
        }

        parentFragmentManager.setFragmentResultListener(
            GetServersDialog.RESULT_KEY, this) { _, bundle ->
            onServersLoaded(bundle)
        }

        parentFragmentManager.setFragmentResultListener(
            PingServersDialog.RESULT_KEY, this) { _, result ->
            onPingUpdated(result)
        }

        setupButtons(view)
        openGetServersDialog()
    }

    private fun onServerMenuAction(bundle: Bundle)
    {
        val action = bundle.getString(MenuServerDialog.EXTRA_ACTION)
        val position = bundle.getInt(MenuServerDialog.EXTRA_POSITION)
        if (position in filteredServers.indices) {
            val server = filteredServers[position]
            when (action) {
                MenuServerDialog.ACTION_CONNECT -> openStatusFragmentWithServer(server)
                MenuServerDialog.ACTION_FAVORITE -> {
                    server.favorite = !server.favorite
                    ServerStorage.saveAll(requireContext(), vpnServers)
                    updateServers()
                }
                MenuServerDialog.ACTION_DELETE -> {
                    vpnServers.removeIf { it.ip == server.ip }
                    ServerStorage.saveAll(requireContext(), vpnServers)
                    updateServers()
                }
            }
        }
    }

    private fun onServersLoaded(bundle: Bundle)
    {
        val csv = bundle.getString(GetServersDialog.RESULT_EXTRA)
        if (!csv.isNullOrBlank()) {
            val reader = csv.reader().buffered()
            val parsed = ServersParser.parseCsv(reader)
            vpnServers.clear()
            vpnServers.addAll(parsed)
            ServerStorage.saveAll(requireContext(), vpnServers)
            updateServersWithPing()
        } else {
            Toast.makeText(requireContext(), "Ошибка загрузки списка!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPingUpdated(result: Bundle) {
        val updatedServers = result.pingServers ?: return
        vpnServers.clear()
        vpnServers.addAll(updatedServers)
        ServerStorage.saveAll(requireContext(), vpnServers)
        updateServers()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .menu.findItem(R.id.nav_servers).isChecked = true

        updateServers()
    }

    private fun setupButtons(view: View) {
        view.findViewById<Button>(R.id.ServersButtonFilter).setOnClickListener {
            openCountryFilterDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonGetServers).setOnClickListener {
            openGetServersDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonAddServer).setOnClickListener {
            openAddServerDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonEmptyServers).setOnClickListener {
            openEmptyServersDialog()
        }
    }

    private fun loadServersFromAssets(context: Context) {
        try {
            context.assets.open(assetCsv).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val parsed = ServersParser.parseCsv(reader)
                    vpnServers.clear()
                    vpnServers.addAll(parsed)
                    ServerStorage.saveAll(requireContext(), vpnServers)
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
        if (parentFragmentManager.findFragmentByTag(GetServersDialog.TAG)?.isAdded == true) return
        GetServersDialog.newInstance(githubCsv)
            .show(parentFragmentManager, GetServersDialog.TAG)
    }

    private fun openEmptyServersDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистка списка серверов")
            .setMessage("Вы действительно хотите очистить список серверов?")
            .setPositiveButton(R.string.clear_all) { _, _ ->
                emptyServers()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun emptyServers() {
        vpnServers.clear()
        ServerStorage.saveAll(requireContext(), vpnServers)
        updateServers()
    }

    private fun updateServersWithPing() {
        val dialog = PingServersDialog.newInstance(vpnServers)
        dialog.show(parentFragmentManager, PingServersDialog.TAG)
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
            compareByDescending<ServerItem> { it.favorite }
                .thenBy { it.ping ?: Int.MAX_VALUE }
        )

        adapter.notifyDataSetChanged()
    }

    private fun extractUniqueCountryCodes(): List<String> {
        return vpnServers.mapNotNull { it.country }.distinct().sorted()
    }

    private fun openCountryFilterDialog() {
        val uniqueCountryCodes = extractUniqueCountryCodes()
        CountryFilterDialog.newInstance(uniqueCountryCodes)
            .show(parentFragmentManager, CountryFilterDialog.TAG)
    }

    private fun openStatusFragmentWithServer(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        LastServerStorage.setAutoConnect(requireContext(), true)
        findNavController().navigate(R.id.nav_status)
    }
}
