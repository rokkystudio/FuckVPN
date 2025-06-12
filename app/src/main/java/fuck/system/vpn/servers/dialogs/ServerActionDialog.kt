package fuck.system.vpn.servers.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerGeo
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServersStorage
import fuck.system.vpn.status.LastServerStorage

/**
 * Диалог действий с конкретным VPN-сервером.
 *
 * Отображает 3 действия:
 * - Подключение к серверу
 * - Добавление/удаление из избранного
 * - Удаление сервера из списка
 *
 * Сервер определяется по IP, переданному в аргументах.
 * Данные загружаются напрямую из ServersStorage.
 */
class ServerActionDialog : DialogFragment()
{
    override fun getTheme(): Int = R.style.DialogTheme

    companion object {
        const val TAG = "MenuServerDialog"
        const val EXTRA_IP = "ip"

        const val CONNECT_REQUEST = "ServerActionDialog.CONNECT"

        /**
         * Создаёт новый экземпляр диалога с передачей IP сервера.
         */
        fun newInstance(ip: String) = ServerActionDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_IP, ip)
            }
        }
    }

    private lateinit var server: ServerItem
    private lateinit var servers: MutableList<ServerItem>

    private lateinit var btnFavorite: MaterialButton
    private lateinit var imageFavorite: ImageView

    /**
     * Загружает layout диалога.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_server_action, container, false)
    }

    /**
     * Вызывается после создания view.
     * Инициализирует данные сервера и UI-элементы.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val ip = requireArguments().getString(EXTRA_IP)
        if (ip == null) {
            exitWithNotFound()
            return
        }

        servers = ServersStorage.load(requireContext())
        server = servers.find { it.ip == ip } ?: run {
            exitWithNotFound()
            return
        }

        val textCountry = view.findViewById<TextView>(R.id.textCountry)
        textCountry.text = ServerGeo.getCountry(server.country)

        val textIp = view.findViewById<TextView>(R.id.textIp)
        textIp.text = server.ip

        val textPing = view.findViewById<TextView>(R.id.textPing)
        textPing.text = getString(R.string.servers_ping_value, server.ping?.toString() ?: "—")

        val imageFlag = view.findViewById<ImageView>(R.id.imageFlag)
        imageFlag.setImageResource(ServerGeo.getFlag(server.country))

        val btnConnect = view.findViewById<MaterialButton>(R.id.btnConnect)
        btnConnect.setOnClickListener {
            onConnectClicked()
        }

        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            onDeleteClicked()
        }

        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnFavorite.setOnClickListener {
            onFavoriteClicked()
        }

        imageFavorite = view.findViewById<ImageView>(R.id.imageFavorite)

        updateFavoriteIcon()
    }

    /**
     * Обновляет иконку и описание кнопки "Избранное" в зависимости от текущего состояния.
     */
    private fun updateFavoriteIcon()
    {
        if (server.favorite) {
            btnFavorite.setIconResource(R.drawable.ic_star_outline)
            btnFavorite.contentDescription = getString(R.string.server_action_remove_favorite)
            btnFavorite.tooltipText = getString(R.string.server_action_remove_favorite)
            imageFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            btnFavorite.setIconResource(R.drawable.ic_star_filled)
            btnFavorite.contentDescription = getString(R.string.server_action_add_favorite)
            btnFavorite.tooltipText = getString(R.string.server_action_add_favorite)
            imageFavorite.setImageResource(R.drawable.ic_star_outline)
        }
    }

    /**
     * Сохраняет выбранный сервер в качестве последнего подключённого
     * и закрывает диалог.
     */
    private fun onConnectClicked() {
        LastServerStorage.save(requireContext(), server)
        parentFragmentManager.setFragmentResult(CONNECT_REQUEST, Bundle())
        dismiss()
    }

    /**
     * Переключает состояние "избранного" и обновляет UI.
     */
    private fun onFavoriteClicked() {
        server.favorite = !server.favorite
        ServersStorage.save(requireContext(), servers)
        updateFavoriteIcon()
    }

    /**
     * Удаляет сервер из списка и сохраняет изменения.
     */
    private fun onDeleteClicked() {
        servers.removeIf { it.ip == server.ip }
        ServersStorage.save(requireContext(), servers)
        dismiss()
    }

    /**
     * Показывает сообщение об ошибке (сервер не найден) и закрывает диалог.
     */
    private fun exitWithNotFound() {
        Toast.makeText(requireContext(), R.string.server_action_not_found, Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
