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
    companion object {
        const val TAG = "MenuServerDialog"
        const val EXTRA_IP = "ip"

        /**
         * Создаёт новый экземпляр диалога с передачей IP сервера.
         */
        fun newInstance(ip: String) = ServerActionDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_IP, ip)
            }
        }
    }

    override fun getTheme(): Int = R.style.DialogTheme

    private lateinit var server: ServerItem
    private lateinit var servers: MutableList<ServerItem>

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

        setupButtons(view)
        setupServerInfo(view)
    }

    /**
     * Назначает обработчики кнопок действий (подключение, удаление, избранное).
     * Обновляет иконку кнопки избранного в соответствии с текущим состоянием.
     */
    private fun setupButtons(view: View)
    {
        val btnConnect = view.findViewById<MaterialButton>(R.id.btnConnect)
        btnConnect.setOnClickListener {
            onConnectClicked()
        }

        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            onDeleteClicked()
        }

        val btnFavorite = view.findViewById<MaterialButton>(R.id.btnFavorite)
        btnFavorite.setOnClickListener {
            onFavoriteClicked()
        }

        updateFavoriteIcon(btnFavorite)
    }

    /**
     * Отображает информацию о сервере:
     * страна, IP, пинг, флаг и текущий статус избранного.
     */
    private fun setupServerInfo(view: View)
    {
        view.findViewById<TextView>(R.id.textCountry).text = ServerGeo.getCountry(server.country)
        view.findViewById<TextView>(R.id.textIp).text = server.ip
        view.findViewById<TextView>(R.id.textPing).text =
            getString(R.string.ping_value, server.ping?.toString() ?: "—")

        view.findViewById<ImageView>(R.id.imageFlag).setImageResource(ServerGeo.getFlag(server.country))

        view.findViewById<ImageView>(R.id.imageFavorite).setImageResource(
            if (server.favorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
    }

    /**
     * Обновляет иконку и описание кнопки "Избранное" в зависимости от текущего состояния.
     */
    private fun updateFavoriteIcon(button: MaterialButton)
    {
        var icon = R.drawable.ic_star_outline
        var description = getString(R.string.server_action_add_favorite)

        if (server.favorite) {
            icon = R.drawable.ic_star_filled
            description = getString(R.string.server_action_remove_favorite)
        }

        button.setIconResource(icon)
        button.contentDescription = description
    }

    /**
     * Сохраняет выбранный сервер в качестве последнего подключённого
     * и закрывает диалог.
     */
    private fun onConnectClicked() {
        LastServerStorage.save(requireContext(), server)
        dismiss()
    }

    /**
     * Переключает состояние "избранного" и обновляет UI.
     */
    private fun onFavoriteClicked() {
        server.favorite = !server.favorite
        ServersStorage.save(requireContext(), servers)
        val button = view?.findViewById<MaterialButton>(R.id.btnFavorite)
        if (button != null) updateFavoriteIcon(button)
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
