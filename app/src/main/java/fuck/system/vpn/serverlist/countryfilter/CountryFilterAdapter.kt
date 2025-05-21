package fuck.system.vpn.serverlist.countryfilter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import fuck.system.vpn.R
import fuck.system.vpn.serverlist.FlagMap

class CountryFilterAdapter(
    private val countries: List<CountryFilterItem>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<CountryFilterAdapter.CountryViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_country_filter_item, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val item = countries[position]

        holder.countryFilterName.text = FlagMap.getCountry(item.country)
        holder.countryFilterCheck.isChecked = item.enabled
        holder.countryFilterFlag.setImageResource(FlagMap.getFlag(item.country))

        holder.itemView.setOnClickListener {
            onItemClicked(position)
        }
    }

    override fun getItemCount(): Int = countries.size

    class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryFilterFlag: ImageView = view.findViewById(R.id.CountryFilterItemFlag)
        val countryFilterName: TextView = view.findViewById(R.id.CountryFilterItemName)
        val countryFilterCheck: CheckBox = view.findViewById(R.id.CountryFilterItemCheck)
    }
}
