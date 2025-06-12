package fuck.system.vpn.servers.filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServersStorage

/**
 * Диалог фильтрации серверов по странам.
 * Позволяет выбрать, какие страны отображать в списке серверов.
 */
class FilterCountryDialog : DialogFragment()
{
    override fun getTheme(): Int = R.style.DialogTheme

    companion object {
        const val TAG = "CountryFilterDialog"
    }

    private var recyclerView: RecyclerView? = null
    private var adapter = FilterCountryAdapter(mutableListOf()) {}

    /** Список стран с состоянием фильтрации (вкл/выкл) */
    private var filters: MutableList<FilterCountryItem> = mutableListOf()

    /**
     * Подключает layout-ресурс
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_filter_country, container, false)
    }

    /**
     * Инициализирует кнопки и список стран.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.CountryFilterList)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.itemAnimator = DefaultItemAnimator()

        view.findViewById<Button>(R.id.CountryFilterButtonSelectAll)
            .setOnClickListener { onSelectAllClicked() }

        view.findViewById<Button>(R.id.CountryFilterButtonUnselectAll)
            .setOnClickListener { onUnselectAllClicked() }

        view.findViewById<Button>(R.id.CountryFilterButtonClose)
            .setOnClickListener { dismiss() }
    }

    /**
     * Загружает данные и обновляет адаптер при открытии диалога.
     */
    override fun onStart() {
        super.onStart()
        prepareData()
        setupAdapter()
    }

    /**
     * Сохраняет выбранные фильтры при закрытии диалога.
     */
    override fun onStop() {
        super.onStop()
        FilterCountryStorage.saveAll(requireContext(), filters)
    }

    /**
     * Отмечает все страны как активные.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun onSelectAllClicked() {
        if (filters.isEmpty()) return
        filters.forEach { it.enabled = true }
        adapter.notifyDataSetChanged()
    }

    /**
     * Отмечает все страны как неактивные.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun onUnselectAllClicked() {
        if (filters.isEmpty()) return
        filters.forEach { it.enabled = false }
        adapter.notifyDataSetChanged()
    }

    /**
     * Формирует список фильтров из всех доступных стран,
     * объединяя сохранённые фильтры и новые страны.
     */
    private fun prepareData()
    {
        val allServers = ServersStorage.load(requireContext())
        val allCountryCodes = allServers.mapNotNull { it.country }.distinct()

        val savedFilters = FilterCountryStorage.loadAll(requireContext())
            .filter { !it.enabled } // Только отключённые
            .toMutableList()

        // Добавляем страны, которых нет в сохранённых
        allCountryCodes.forEach { code ->
            if (savedFilters.none { it.country == code }) {
                savedFilters.add(FilterCountryItem(code, true))
            }
        }

        filters = savedFilters
            .distinctBy { it.country }
            .sortedBy { it.country }
            .toMutableList()
    }

    /**
     * Создаёт и обновляет адаптер с чекбоксами стран.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter()
    {
        adapter = FilterCountryAdapter(filters) { position ->
            filters[position].enabled = !filters[position].enabled
            adapter.notifyItemChanged(position)
        }
        recyclerView?.adapter = adapter
    }
}
