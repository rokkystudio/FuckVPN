package fuck.system.vpn.status

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerGeo
import fuck.system.vpn.servers.server.ServerItem

/**
 * Фрагмент отображения статуса текущего VPN-соединения:
 * - Показывает информацию о сервере
 * - Позволяет подключаться и отключаться
 * - Отображает текущий ping (RTT), обновляемый в реальном времени
 */
class StatusFragment : Fragment(R.layout.fragment_status) {

    private var pendingServer: ServerItem? = null
    private var currentServer: ServerItem? = null

    private lateinit var textName: TextView
    private lateinit var textIp: TextView
    private lateinit var textPort: TextView
    private lateinit var textCountry: TextView
    private lateinit var textPing: TextView
    private lateinit var imageFlag: ImageView
    private lateinit var buttonConnectDisconnect: Button

    private var pingUpdateHandler: Handler? = null
    private val pingUpdateRunnable = object : Runnable {
        override fun run() {
            if (isVpnConnected()) {
                val ping = OpenVPNService.currentPing
                textPing.text = if (ping > 0) "Ping: ${ping}ms" else "Ping: -"
                pingUpdateHandler?.postDelayed(this, 1000)
            } else {
                textPing.text = "Ping: -"
            }
        }
    }

    private val vpnDisconnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                cleanOldProfiles(it)
                pendingServer?.let { server ->
                    realStartVpn(server)
                    pendingServer = null
                }
            }
        }
    }

    private val vpnStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateVpnStatus()
        }
    }

    /**
     * Инициализация UI и слушателей
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.StatusServerName)
        textIp = view.findViewById(R.id.StatusServerIp)
        textPort = view.findViewById(R.id.StatusServerPort)
        textCountry = view.findViewById(R.id.StatusServerCountry)
        textPing = view.findViewById(R.id.StatusServerPing)
        imageFlag = view.findViewById(R.id.StatusServerFlag)
        buttonConnectDisconnect = view.findViewById(R.id.StatusConnectDisconnect)

        ContextCompat.registerReceiver(
            requireContext(),
            vpnDisconnectedReceiver,
            IntentFilter("de.blinkt.openvpn.DISCONNECTED"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            requireContext(),
            vpnStatusReceiver,
            IntentFilter("de.blinkt.openvpn.VPN_STATUS"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        buttonConnectDisconnect.setOnClickListener {
            currentServer?.let { server ->
                if (isVpnConnected()) stopVpn() else startVpn(server)
            }
        }
    }

    /**
     * Запускается при возвращении на экран: восстанавливает сервер, статус и запускает обновление пинга
     */
    override fun onResume() {
        super.onResume()

        val server = LastServerStorage.load(requireContext())
        if (server == null) {
            clearViews()
            return
        }

        currentServer = server
        fillViews(server)

        if (LastServerStorage.getAutoConnect(requireContext())) {
            LastServerStorage.setAutoConnect(requireContext(), false)
            startVpn(server)
        }

        updateVpnStatus()

        pingUpdateHandler = Handler(Looper.getMainLooper())
        pingUpdateHandler?.post(pingUpdateRunnable)
    }

    /**
     * Останавливает обновление пинга при выходе с экрана
     */
    override fun onPause() {
        super.onPause()
        pingUpdateHandler?.removeCallbacks(pingUpdateRunnable)
        pingUpdateHandler = null
    }

    /**
     * Очистка и отписка от BroadcastReceiver'ов
     */
    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(vpnDisconnectedReceiver)
        requireContext().unregisterReceiver(vpnStatusReceiver)
    }

    // ---- VPN ----

    /**
     * Запускает VPN, предварительно сохранив сервер и остановив текущий VPN (если есть)
     */
    fun startVpn(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        pendingServer = server
        stopVpn()
    }

    /**
     * Отправляет сигнал на отключение VPN
     */
    fun stopVpn() {
        val stopIntent = Intent(requireContext(), OpenVPNService::class.java)
        stopIntent.action = OpenVPNService.DISCONNECT_VPN
        requireContext().applicationContext.startService(stopIntent)
    }

    /**
     * Реальное подключение после выключения старого соединения
     */
    private fun realStartVpn(server: ServerItem) {
        val context = requireContext().applicationContext
        val profile = importProfileFromOvpn(server)
        ProfileManager.getInstance(context).addProfile(profile)
        ProfileManager.saveProfile(context, profile)
        val intent = profile.getStartServiceIntent(context, "manual", true)
        context.startService(intent)
    }

    /**
     * Импортирует VPN-профиль из конфигурации .ovpn
     */
    private fun importProfileFromOvpn(server: ServerItem): VpnProfile {
        val profile = VpnProfile(server.name)
        profile.mUseCustomConfig = true
        profile.mCustomConfigOptions = server.ovpn
        return profile
    }

    /**
     * Очищает старые VPN-профили
     */
    private fun cleanOldProfiles(context: Context) {
        val manager = ProfileManager.getInstance(context)
        val allProfiles = manager.profiles.toList()
        allProfiles.forEach { profile ->
            manager.removeProfile(context, profile)
        }
    }

    // ---- UI ----

    /**
     * Заполняет UI-информацией о текущем сервере
     */
    private fun fillViews(server: ServerItem) {
        textName.text = server.name
        textIp.text = "IP: ${server.ip ?: "-"}"
        textPort.text = "Port: ${server.port?.toString() ?: "-"}"
        textCountry.text = ServerGeo.getCountry(server.country)
        textPing.text = "Ping: -"
        imageFlag.setImageResource(ServerGeo.getFlag(server.country))
        buttonConnectDisconnect.isEnabled = true
    }

    /**
     * Очищает все текстовые поля и блокирует кнопку подключения
     */
    private fun clearViews() {
        textName.text = ""
        textIp.text = ""
        textPort.text = ""
        textCountry.text = ""
        textPing.text = ""
        imageFlag.setImageDrawable(null)
        buttonConnectDisconnect.text = getString(R.string.status_connect)
        buttonConnectDisconnect.isEnabled = false
    }

    /**
     * Обновляет состояние кнопки подключения в зависимости от статуса VPN
     */
    private fun updateVpnStatus() {
        if (isVpnConnected()) {
            buttonConnectDisconnect.text = getString(R.string.status_disconnect)
        } else {
            buttonConnectDisconnect.text = getString(R.string.status_connect)
        }
        buttonConnectDisconnect.isEnabled = currentServer != null
    }

    /**
     * Проверяет, активно ли VPN-соединение
     */
    private fun isVpnConnected(): Boolean {
        return OpenVPNService.isConnected()
    }
}
