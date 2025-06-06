package fuck.system.vpn.servers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import fuck.system.vpn.servers.dialogs.AddServerDialog
import fuck.system.vpn.servers.filters.CountryFilterDialog
import fuck.system.vpn.servers.filters.CountryFilterStorage
import fuck.system.vpn.servers.dialogs.PingServersDialog
import fuck.system.vpn.servers.dialogs.MenuServerDialog
import fuck.system.vpn.servers.server.ServerAdapter
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServersStorage
import fuck.system.vpn.status.LastServerStorage

/**
 * Фрагмент, отображающий список VPN-серверов, с возможностью обновления, фильтрации,
 * добавления, удаления, а также запуска диалогов пинга и получения новых серверов.
 */
class ServersFragment : Fragment(R.layout.fragment_servers)
{
    /** Виджет со списком серверов */
    private lateinit var recyclerView: RecyclerView

    /** Адаптер для отображения серверов */
    private lateinit var adapter: ServerAdapter

    /** Основной список всех серверов */
    private val vpnServers = mutableListOf<ServerItem>()

    /** Отфильтрованные серверы, отображаемые на экране */
    private var filteredServers = mutableListOf<ServerItem>()

    /**
     * Слушатель изменений SharedPreferences для серверов
     */
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "servers") {
            onServersChangedFromStorage()
        }
    }

    /**
     * Вызывается при изменении данных в SharedPreferences
     */
    private fun onServersChangedFromStorage()
    {
        val context = requireContext()
        val newServers = ServersStorage.load(context)
        val oldIps = vpnServers.mapNotNull { it.ip }.toSet()
        val newIps = newServers.mapNotNull { it.ip }.toSet()

        val added = newIps - oldIps
        val removed = oldIps - newIps

        if (added.isNotEmpty() || removed.isNotEmpty()) {
            onServersChanged(newServers)
        } else {
            onServersUpdated(newServers)
        }
    }

    /**
     * Обнаружено добавление или удаление серверов
     */
    private fun onServersChanged(newServers: List<ServerItem>)
    {
        vpnServers.clear()
        vpnServers.addAll(newServers)
        updateServersAdapter()

        PingServersDialog().show(parentFragmentManager, PingServersDialog.TAG)
    }

    /**
     * Обновляет данные серверов в списке vpnServers на основе newServers.
     * Список не пересоздаётся, порядок сохраняется. Обновляются только поля по совпадающему IP.
     */
    private fun onServersUpdated(newServers: List<ServerItem>) {
        val updatesByIp = newServers.associateBy { it.ip }

        for (server in vpnServers) {
            val updated = updatesByIp[server.ip]
            if (updated != null) {
                server.name = updated.name
                server.ovpn = updated.ovpn
                server.favorite = updated.favorite
                server.country = updated.country
                server.ping = updated.ping
                server.port = updated.port
                server.proto = updated.proto
            }
        }

        updateServersAdapter()
    }

    /**
     * Регистрирует наблюдение за изменением списка серверов
     */
    override fun onStart() {
        super.onStart()
        ServersStorage.observe(requireContext(), prefsListener)
    }

    /**
     * Удаляет наблюдение за изменением списка серверов
     */
    override fun onStop() {
        ServersStorage.removeObserver(requireContext(), prefsListener)
        super.onStop()
    }

    /**
     * Инициализация UI после создания View
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
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
            updateServersAdapter()
        }

        parentFragmentManager.setFragmentResultListener(
            MenuServerDialog.RESULT_KEY, this) { _, bundle ->
            onServerMenuAction(bundle)
        }

        setupButtons(view)
    }

    /**
     * Отмечает текущую вкладку как активную
     */
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .menu.findItem(R.id.nav_servers).isChecked = true

        updateServersAdapter()
    }

    /**
     * Привязывает обработчики к кнопкам
     */
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

    /**
     * Обработка действия из контекстного меню сервера
     */
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
                    ServersStorage.save(requireContext(), vpnServers)
                    updateServersAdapter()
                }
                MenuServerDialog.ACTION_DELETE -> {
                    vpnServers.removeIf { it.ip == server.ip }
                    ServersStorage.save(requireContext(), vpnServers)
                    updateServersAdapter()
                }
            }
        }
    }

    /**
     * Открывает диалог добавления нового сервера
     */
    private fun openAddServerDialog() {
        if (parentFragmentManager.findFragmentByTag(AddServerDialog.TAG)?.isAdded != true) {
            AddServerDialog().show(parentFragmentManager, AddServerDialog.TAG)
        }
    }

    /**
     * Открывает диалог загрузки серверов
     */
    private fun openGetServersDialog() {
        //if (parentFragmentManager.findFragmentByTag(GetServersDialog.TAG)?.isAdded != true) {
          //  GetServersDialog().show(parentFragmentManager, GetServersDialog.TAG)
        //}
    }

    /**
     * Показывает подтверждение очистки списка серверов
     */
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

    /**
     * Полная очистка списка серверов
     */
    private fun emptyServers() {
        vpnServers.clear()
        ServersStorage.save(requireContext(), vpnServers)
        updateServersAdapter()
    }

    /** Обновляет список отображаемых серверов с учётом фильтров */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateServersAdapter()
    {
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

    /**
     * Извлекает список уникальных стран из серверов
     */
    private fun extractUniqueCountryCodes(): List<String> {
        return vpnServers.mapNotNull { it.country }.distinct().sorted()
    }

    /**
     * Открывает диалог фильтрации по странам
     */
    private fun openCountryFilterDialog() {
        val uniqueCountryCodes = extractUniqueCountryCodes()
        CountryFilterDialog.newInstance(uniqueCountryCodes)
            .show(parentFragmentManager, CountryFilterDialog.TAG)
    }

    /**
     * Переход к статусу и подключению к выбранному серверу
     */
    private fun openStatusFragmentWithServer(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        LastServerStorage.setAutoConnect(requireContext(), true)
        findNavController().navigate(R.id.nav_status)
    }
}
