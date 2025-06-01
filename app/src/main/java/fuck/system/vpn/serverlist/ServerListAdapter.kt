package fuck.system.vpn.serverlist;

import android.animation.ArgbEvaluator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fuck.system.vpn.R
import androidx.core.graphics.toColorInt

class ServerAdapter(private val servers: List<ServerListItem>) :
    RecyclerView.Adapter<ServerAdapter.ServerViewHolder>()
{

    class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCountry: TextView = itemView.findViewById(R.id.textCountry)
        val textIp: TextView = itemView.findViewById(R.id.textIp)
        val textPing: TextView = itemView.findViewById(R.id.textPing)
        val imageFlag: ImageView = itemView.findViewById(R.id.imageFlag)
        val imageFavorite: ImageView = itemView.findViewById(R.id.imageFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]

        holder.textCountry.text = FlagMap.getCountry(server.country)
        holder.textIp.text = server.ip
        holder.textPing.text = holder.textPing.context.getString(R.string.ping_value, server.ping)


        holder.textPing.setTextColor(getPingGradientColor(server.ping))
        holder.imageFlag.setImageResource(FlagMap.getFlag(server.country))

        // Установка иконки избранного
        var starRes = R.drawable.ic_servers_star_outline
        if (server.favorite) starRes = R.drawable.ic_servers_star_filled
        holder.imageFavorite.setImageResource(starRes)
    }

    override fun getItemCount(): Int = servers.size

    private fun getPingGradientColor(ping: Int?): Int
    {
        val evaluator = ArgbEvaluator()

        val green = "#448844".toColorInt()   // 0 ms
        val yellow = "#CCCC44".toColorInt()  // 100 ms
        val orange = "#CC8844".toColorInt()  // 250 ms
        val red = "#CC4444".toColorInt()     // 500+ ms

        return when {
            ping == null -> red
            ping <= 0 -> green
            ping <= 100 -> evaluator.evaluate(ping / 100f, green, yellow) as Int
            ping <= 250 -> evaluator.evaluate((ping - 100) / 150f, yellow, orange) as Int
            ping <= 500 -> evaluator.evaluate((ping - 250) / 250f, orange, red) as Int
            else -> red
        }
    }
}
