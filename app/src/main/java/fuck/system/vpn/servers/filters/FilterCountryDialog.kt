package fuck.system.vpn.servers.filters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerStorage

class FilterCountryDialog : DialogFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FilterCountryAdapter
    private var adapterFilters: MutableList<FilterCountryItem> = mutableListOf()

    companion object {
        const val TAG = "CountryFilterDialog"
    }

    @Suppress("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_filter_country, null, false)

        setupRecyclerView(view)
        setupButtons(view)

        builder.setView(view)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        prepareData()
        setupAdapter()
    }

    override fun onStop() {
        super.onStop()
        FilterCountryStorage.saveAll(requireContext(), adapterFilters)
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.CountryFilterList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupButtons(view: View) {
        view.findViewById<Button>(R.id.CountryFilterButtonSelectAll)
            .setOnClickListener { onSelectAllClicked() }

        view.findViewById<Button>(R.id.CountryFilterButtonUnselectAll)
            .setOnClickListener { onUnselectAllClicked() }

        view.findViewById<Button>(R.id.CountryFilterButtonClose)
            .setOnClickListener { onCloseClicked() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSelectAllClicked() {
        if (adapterFilters.isEmpty()) return
        adapterFilters.forEach { it.enabled = true }
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onUnselectAllClicked() {
        if (adapterFilters.isEmpty()) return
        adapterFilters.forEach { it.enabled = false }
        adapter.notifyDataSetChanged()
    }

    private fun onCloseClicked() {
        dismiss()
    }

    private fun prepareData()
    {
        val allServers = ServerStorage.load(requireContext())
        val allCountryCodes = allServers.mapNotNull { it.country }.distinct()

        val savedFilters = FilterCountryStorage.loadAll(requireContext())
            .filter { !it.enabled } // Только отключённые
            .toMutableList()

        // Добавляем новые страны с enabled = true, если их нет в сохранённых
        allCountryCodes.forEach { code ->
            if (savedFilters.none { it.country == code }) {
                savedFilters.add(FilterCountryItem(code, true))
            }
        }

        adapterFilters = savedFilters
            .distinctBy { it.country }
            .sortedBy { it.country }
            .toMutableList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {
        if (!::adapter.isInitialized) {
            adapter = FilterCountryAdapter(adapterFilters) { position ->
                adapterFilters[position].enabled = !adapterFilters[position].enabled
                adapter.notifyItemChanged(position)
            }
            recyclerView.adapter = adapter
        } else {
            adapter.notifyDataSetChanged()
        }
    }
}
