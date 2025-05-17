package fuck.system.vpn.serverlist;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R

class ServerAdapter(private val servers: List<VpnServer>) :
    RecyclerView.Adapter<ServerAdapter.ServerViewHolder>()
{
    class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCountry: TextView = itemView.findViewById(R.id.textCountry)
        val textPing: TextView = itemView.findViewById(R.id.textPing)
        val imageFlag: ImageView = itemView.findViewById(R.id.imageFlag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]
        holder.textCountry.text = server.country
        holder.textPing.text = "${server.ping} ms"

        val flagName = "flag_${server.countryCode.lowercase()}"
        val flagResId = holder.itemView.context.resources.getIdentifier(flagName, "drawable", holder.itemView.context.packageName)
        holder.imageFlag.setImageResource(if (flagResId != 0) flagResId else R.drawable.flag_xx)
    }

    override fun getItemCount() = servers.size
}