package fuck.system.vpn.servers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import fuck.system.vpn.servers.dialogs.ServerCreateDialog
import fuck.system.vpn.servers.dialogs.ServersUpdateDialog
import fuck.system.vpn.servers.filters.FilterCountryDialog
import fuck.system.vpn.servers.filters.FilterCountryStorage
import fuck.system.vpn.servers.ping.ServersPingDialog
import fuck.system.vpn.servers.dialogs.ServerActionDialog
import fuck.system.vpn.servers.dialogs.ServerActionDialog.Companion.CONNECT_REQUEST
import fuck.system.vpn.servers.dialogs.ServersClearDialog
import fuck.system.vpn.servers.server.ServerAdapter
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServersStorage


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
    private val vpnServer = mutableListOf<ServerItem>()

    /** Отфильтрованные серверы, отображаемые на экране */
    private var filterServer = mutableListOf<ServerItem>()


    /** Запланированный отложенный первичный запуск */
    private var shouldInitialAction = true

    /** Запланирован запуск обновления адаптера */
    private var shouldAdapterUpdate = false

    /** Запланирован запуск обновления пинга */
    private var shouldServerPing = false

    /**
     * Слушатель изменений SharedPreferences для списка серверов.
     * Срабатывает при любом обновлении значения по ключу [ServersStorage.KEY_SERVERS],
     * например, после загрузки новых серверов или их редактирования вручную.
     * Вызывает обновление UI и/или дополнительную обработку.
     */
    private val serverObserver = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == ServersStorage.KEY_SERVERS) {
            onServerChanged()
        }
    }

    /**
     * Слушатель изменений фильтра стран в SharedPreferences.
     * Срабатывает при изменении значения по ключу [FilterCountryStorage.KEY_FILTER],
     * что может произойти, например, после выбора страны в фильтре.
     * Вызывает обновление адаптера отображаемых серверов.
     */
    private val countryObserver = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == FilterCountryStorage.KEY_FILTER) {
            updateAdapter()
        }
    }

    /**
     * Обрабатывает изменения в хранилище серверов.
     * Загружает новые данные, обновляет список и адаптер, а также
     * при необходимости запускает диалог пинга.
     */
    private fun onServerChanged()
    {
        val context = context ?: return
        val newServers = ServersStorage.load(context)

        vpnServer.apply {
            clear()
            addAll(newServers)
        }

        if (shouldServerPing) {
            shouldServerPing = false
            if (parentFragmentManager.isStateSaved.not()) {
                ServersPingDialog().show(parentFragmentManager, ServersPingDialog.TAG)
            }
        }

        updateAdapter()
    }

    /**
     * Обновляет список отображаемых серверов с учётом фильтров
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter()
    {
        if (!isResumed) {
            shouldAdapterUpdate = true
            return
        }

        shouldAdapterUpdate = false

        val countryFilters = FilterCountryStorage.loadAll(requireContext())

        filterServer.clear()
        filterServer.addAll(
            vpnServer.filter { server ->
                val filter = countryFilters.find { it.country == server.country }
                filter == null || filter.enabled
            }
        )

        filterServer.sortWith(
            compareByDescending<ServerItem> { it.favorite }
                .thenBy { it.ping ?: Int.MAX_VALUE }
        )

        adapter.notifyDataSetChanged()
    }

    /**
     * Выполняем отложенные запуски
     */
    override fun onResume() {
        super.onResume()

        if (shouldAdapterUpdate) {
            updateAdapter()
        }
    }

    /**
     * Регистрирует наблюдение за изменением списка серверов
     */
    override fun onStart() {
        super.onStart()
        ServersStorage.observe(requireContext(), serverObserver)
        FilterCountryStorage.observe(requireContext(), countryObserver)
    }

    /**
     * Удаляет наблюдение за изменением списка серверов
     */
    override fun onStop() {
        ServersStorage.removeObserver(requireContext(), serverObserver)
        FilterCountryStorage.removeObserver(requireContext(), countryObserver)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(CONNECT_REQUEST, this) { _, _ ->
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.nav_status
        }
    }

    /**
     * Инициализация UI после создания View
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerServers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ServerAdapter(filterServer) { server ->
            openServerActionDialog(server)
        }
        recyclerView.adapter = adapter

        view.postDelayed({ onInitialAction() }, 1000)

        setupButtons(view)

        onServerChanged()
    }


    override fun onDestroyView() {
        shouldInitialAction = false
        super.onDestroyView()
    }

    /**
     * Выполняет начальное действие, когда фрагмент полностью готов.
     * Исключает повторные и небезопасные вызовы при возможном уничтожении фрагмента.
     */
    private fun onInitialAction()
    {
        if (!shouldInitialAction) return
        if (!isAdded) return
        if (parentFragmentManager.isStateSaved) return

        shouldInitialAction = false
        openServersUpdateDialog()
    }

    /**
     * Привязывает обработчики к кнопкам
     */
    private fun setupButtons(view: View) {
        view.findViewById<Button>(R.id.ServersButtonFilter).setOnClickListener {
            openCountryFilterDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonGetServers).setOnClickListener {
            openServersUpdateDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonAddServer).setOnClickListener {
            openServerCreateDialog()
        }

        view.findViewById<Button>(R.id.ServersButtonEmptyServers).setOnClickListener {
            openServersClearDialog()
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
    private fun openServerCreateDialog() {
        if (parentFragmentManager.findFragmentByTag(ServerCreateDialog.TAG)?.isAdded != true) {
            shouldServerPing = true
            ServerCreateDialog().show(parentFragmentManager, ServerCreateDialog.TAG)
        }
    }

    /**
     * Открывает диалог загрузки серверов
     */
    private fun openServersUpdateDialog() {
        if (parentFragmentManager.findFragmentByTag(ServersUpdateDialog.TAG)?.isAdded != true) {
            shouldServerPing = true
            ServersUpdateDialog().show(parentFragmentManager, ServersUpdateDialog.TAG)
        }
    }

    /**
     * Показывает диалог подтверждения очистки списка серверов
     */
    private fun openServersClearDialog() {
        if (parentFragmentManager.findFragmentByTag(ServersClearDialog.TAG)?.isAdded != true) {
            ServersClearDialog().show(parentFragmentManager, ServersClearDialog.TAG)
        }
    }

    /**
     * Показывает диалог действия при клике по серверу в списке
     */
    private fun openServerActionDialog(server: ServerItem) {
        val ip = server.ip ?: return
        if (parentFragmentManager.findFragmentByTag(ServerActionDialog.TAG)?.isAdded != true) {
            ServerActionDialog.newInstance(ip)
                .show(parentFragmentManager, ServerActionDialog.TAG)
        }
    }
}
