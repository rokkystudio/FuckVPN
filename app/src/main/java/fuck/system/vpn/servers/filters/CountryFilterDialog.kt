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
import kotlin.math.min

class CountryFilterDialog : DialogFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryFilterAdapter
    private var adapterFilters: MutableList<CountryFilterItem> = mutableListOf()

    companion object {
        const val TAG = "CountryFilterDialog"
        const val KEY = "CountryFilterKey"

        fun newInstance(countryCodes: List<String>): CountryFilterDialog {
            val fragment = CountryFilterDialog()
            val bundle = Bundle()
            bundle.putStringArrayList(KEY, ArrayList(countryCodes))
            fragment.arguments = bundle
            return fragment
        }
    }

    @Suppress("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_country_filter, null, false)

        setupRecyclerView(view)
        setupButtons(view)
        prepareData()
        setupAdapter()
        adjustRecyclerViewHeight()

        builder.setView(view)
        return builder.create()
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult(KEY, Bundle())
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.CountryFilterList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupButtons(view: View)
    {
        view.findViewById<Button>(
            R.id.CountryFilterButtonSelectAll)
            .setOnClickListener {
                onSelectAllClicked()
            }

        view.findViewById<Button>(
            R.id.CountryFilterButtonUnselectAll)
            .setOnClickListener {
                onUnselectAllClicked()
            }

        view.findViewById<Button>(
            R.id.CountryFilterButtonClose)
            .setOnClickListener {
                onCloseClicked()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSelectAllClicked() {
        if (adapterFilters.isEmpty()) return
        adapterFilters.forEach { it.enabled = true }
        CountryFilterStorage.saveAll(requireContext(), adapterFilters)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onUnselectAllClicked() {
        if (adapterFilters.isEmpty()) return
        adapterFilters.forEach { it.enabled = false }
        CountryFilterStorage.saveAll(requireContext(), adapterFilters)
        adapter.notifyDataSetChanged()
    }

    private fun onCloseClicked() {
        dismiss()
    }

    private fun prepareData() {
        val usingCountries = arguments?.getStringArrayList(KEY) ?: arrayListOf()
        val savedFilters = CountryFilterStorage.loadAll(requireContext()).toMutableList()

        usingCountries.forEach { code ->
            if (savedFilters.none { it.country == code }) {
                savedFilters.add(CountryFilterItem(code, true))
            }
        }

        CountryFilterStorage.saveAll(requireContext(), savedFilters)

        adapterFilters = savedFilters
            .filter { it.country in usingCountries || !it.enabled }
            .sortedBy { it.country }
            .toMutableList()
    }

    private fun adjustRecyclerViewHeight() {
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val maxDialogHeight = (screenHeight * 0.7).toInt()

        recyclerView.post {
            recyclerView.layoutParams?.let { params ->
                params.height = min(recyclerView.measuredHeight, maxDialogHeight)
                recyclerView.layoutParams = params
                recyclerView.requestLayout()
            }
        }
    }

    private fun setupAdapter() {
        adapter = CountryFilterAdapter(adapterFilters, ::onCountryItemClicked)
        recyclerView.adapter = adapter
    }

    private fun onCountryItemClicked(position: Int) {
        val item = adapterFilters[position]
        item.enabled = !item.enabled
        CountryFilterStorage.saveItem(requireContext(), item)
        adapter.notifyItemChanged(position)
    }
}
