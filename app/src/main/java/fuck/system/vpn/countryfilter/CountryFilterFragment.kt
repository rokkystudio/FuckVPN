package fuck.system.vpn.countryfilter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fuck.system.vpn.R
import androidx.core.view.get
import androidx.core.view.size

class CountryFilterFragment : Fragment(R.layout.fragment_country_filter)
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryFilterAdapter
    private lateinit var adapterFilters: List<CountryFilterItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.CountryFilterList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val usingCountries = arguments?.getStringArrayList("country_codes") ?: listOf()

        val savedFilters = CountryFilterStorage.loadAll(requireContext()).toMutableList()

        // Добавляем новые страны из usingCountries, которых ещё нет в savedFilters
        usingCountries.forEach { code ->
            if (savedFilters.none { it.country == code }) {
                savedFilters.add(CountryFilterItem(code, true))
            }
        }

        // Сохраняем обновлённый список
        CountryFilterStorage.saveAll(requireContext(), savedFilters)

        // Фильтруем и сортируем итоговый список для адаптера
        val adapterFilters = savedFilters
            .filter { it.country in usingCountries || !it.enabled }
            .sortedBy { it.country }

        adapter = CountryFilterAdapter(adapterFilters, ::onCountryItemClicked)
        recyclerView.adapter = adapter

        unsetNavigationSelection()
    }

    private fun unsetNavigationSelection() {
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until navView.menu.size) {
            navView.menu[i].isChecked = false
        }
        navView.menu.setGroupCheckable(0, true, true)
    }

    private fun onCountryItemClicked(position: Int) {
        val item = adapterFilters[position]
        item.enabled = !item.enabled
        CountryFilterStorage.saveItem(requireContext(), item)
        adapter.notifyItemChanged(position)
    }
}
