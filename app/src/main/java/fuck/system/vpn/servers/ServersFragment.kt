package fuck.system.vpn.servers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import fuck.system.vpn.servers.dialogs.ServerCreateDialog
import fuck.system.vpn.servers.dialogs.ServerUpdateDialog
import fuck.system.vpn.servers.filters.FilterCountryDialog
import fuck.system.vpn.servers.filters.FilterCountryStorage
import fuck.system.vpn.servers.ping.PingDialog
import fuck.system.vpn.servers.dialogs.ServerActionDialog
import fuck.system.vpn.servers.server.ServerAdapter
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServerStorage
import fuck.system.vpn.status.LastServerStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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


    private var isDialogScheduled = false

    /**
     * Слушатель изменений SharedPreferences для серверов
     */
    private val serversObserver = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == ServerStorage.KEY_SERVERS) {
            onServersChangedFromStorage()
        }
    }

    private val countryObserver = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == FilterCountryStorage.KEY_FILTER) {
            updateServersAdapter()
        }
    }

    /**
     * Вызывается при изменении данных в SharedPreferences
     */
    private fun onServersChangedFromStorage()
    {
        val context = requireContext()
        val newServers = ServerStorage.load(context)
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

        PingDialog().show(parentFragmentManager, PingDialog.TAG)
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
        ServerStorage.observe(requireContext(), serversObserver)
        FilterCountryStorage.observe(requireContext(), countryObserver)
    }

    /**
     * Удаляет наблюдение за изменением списка серверов
     */
    override fun onStop() {
        ServerStorage.removeObserver(requireContext(), serversObserver)
        FilterCountryStorage.removeObserver(requireContext(), countryObserver)
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
                ServerActionDialog.newInstance(isFavorite, position)
                    .show(parentFragmentManager, ServerActionDialog.TAG)
            }
        })
        recyclerView.adapter = adapter

        parentFragmentManager.setFragmentResultListener(
            ServerActionDialog.RESULT_KEY, this) { _, bundle ->
            onServerMenuAction(bundle)
        }

        setupButtons(view)
        scheduleGetServersDialog();
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
        val action = bundle.getString(ServerActionDialog.EXTRA_ACTION)
        val position = bundle.getInt(ServerActionDialog.EXTRA_POSITION)
        if (position in filteredServers.indices) {
            val server = filteredServers[position]
            when (action) {
                ServerActionDialog.ACTION_CONNECT -> openStatusFragmentWithServer(server)
                ServerActionDialog.ACTION_FAVORITE -> {
                    server.favorite = !server.favorite
                    ServerStorage.save(requireContext(), vpnServers)
                    updateServersAdapter()
                }
                ServerActionDialog.ACTION_DELETE -> {
                    vpnServers.removeIf { it.ip == server.ip }
                    ServerStorage.save(requireContext(), vpnServers)
                    updateServersAdapter()
                }
            }
        }
    }

    /**
     * Открывает диалог фильтрации по странам
     */
    private fun openCountryFilterDialog() {
        if (parentFragmentManager.findFragmentByTag(FilterCountryDialog.TAG)?.isAdded != true) {
            FilterCountryDialog().show(parentFragmentManager, FilterCountryDialog.TAG)
        }
    }


    /**
     * Открывает диалог добавления нового сервера
     */
    private fun openAddServerDialog() {
        if (parentFragmentManager.findFragmentByTag(ServerCreateDialog.TAG)?.isAdded != true) {
            ServerCreateDialog().show(parentFragmentManager, ServerCreateDialog.TAG)
        }
    }

    /**
     * Открывает диалог загрузки серверов
     */
    private fun openGetServersDialog() {
        if (parentFragmentManager.findFragmentByTag(ServerUpdateDialog.TAG)?.isAdded != true) {
          ServerUpdateDialog().show(parentFragmentManager, ServerUpdateDialog.TAG)
        }
    }

    private fun scheduleGetServersDialog() {
        if (isDialogScheduled) return
        isDialogScheduled = true

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(1000)
                if (isAdded && parentFragmentManager.isStateSaved.not()) {
                    openGetServersDialog()
                }
            }
        }
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
        ServerStorage.save(requireContext(), vpnServers)
        updateServersAdapter()
    }

    /** Обновляет список отображаемых серверов с учётом фильтров */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateServersAdapter()
    {
        val countryFilters = FilterCountryStorage.loadAll(requireContext())

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
     * Переход к статусу и подключению к выбранному серверу
     */
    private fun openStatusFragmentWithServer(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        LastServerStorage.setAutoConnect(requireContext(), true)
        findNavController().navigate(R.id.nav_status)
    }
}
