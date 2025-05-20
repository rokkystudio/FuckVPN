package fuck.system.vpn.countryfilter

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R

class CountryFilterDialog : DialogFragment()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryFilterAdapter
    private lateinit var adapterFilters: MutableList<CountryFilterItem>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_country_filter, null)

        recyclerView = view.findViewById(R.id.CountryFilterList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val usingCountries = arguments?.getStringArrayList("country_codes") ?: arrayListOf()

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

        adapter = CountryFilterAdapter(adapterFilters, ::onCountryItemClicked)
        recyclerView.adapter = adapter

        builder
            .setView(view)
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }

    private fun onCountryItemClicked(position: Int) {
        val item = adapterFilters[position]
        item.enabled = !item.enabled
        CountryFilterStorage.saveItem(requireContext(), item)
        adapter.notifyItemChanged(position)
    }

    companion object {
        const val TAG = "CountryFilterDialog"

        fun newInstance(countryCodes: List<String>): CountryFilterDialog {
            val fragment = CountryFilterDialog()
            val bundle = Bundle()
            bundle.putStringArrayList("country_codes", ArrayList(countryCodes))
            fragment.arguments = bundle
            return fragment
        }
    }
}