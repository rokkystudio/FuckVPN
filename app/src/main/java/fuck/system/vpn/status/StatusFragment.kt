package fuck.system.vpn.status

import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import fuck.system.vpn.servers.server.ServerFlag

class StatusFragment : Fragment(R.layout.fragment_status)
{
    private var pendingServer: ServerItem? = null
    private var currentServer: ServerItem? = null

    private var pingObserver: PingObserver? = null

    private lateinit var textName: TextView
    private lateinit var textIp: TextView
    private lateinit var textPort: TextView
    private lateinit var textCountry: TextView
    private lateinit var textPing: TextView
    private lateinit var imageFlag: ImageView
    private lateinit var buttonConnectDisconnect: Button

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

    // Просто обновляем статус — детали (true/false) получаем через OpenVPNService.isConnected()
    private val vpnStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateVpnStatus()
        }
    }

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

    override fun onResume() {
        super.onResume()
        pingObserver?.stop()
        pingObserver = null

        val server = LastServerStorage.load(requireContext())
        if (server == null) {
            clearViews()
            return
        }

        currentServer = server
        fillViews(server)

        // Блок автоподключения
        if (LastServerStorage.getAutoConnect(requireContext())) {
            LastServerStorage.setAutoConnect(requireContext(), false)
            startVpn(server)
        }

        updateVpnStatus()

        if (server.ip != null) {
            pingObserver = PingObserver(server.ip)
            pingObserver?.observe(viewLifecycleOwner) { result ->
                textPing.text = result
            }
            pingObserver?.start(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onPause() {
        super.onPause()
        pingObserver?.stop()
        pingObserver = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(vpnDisconnectedReceiver)
        requireContext().unregisterReceiver(vpnStatusReceiver)
    }

    // ---- VPN ----
    fun startVpn(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        pendingServer = server
        stopVpn()
        // После отключения receiver вызовет realStartVpn()
    }

    fun stopVpn() {
        val stopIntent = Intent(requireContext(), OpenVPNService::class.java)
        stopIntent.action = OpenVPNService.DISCONNECT_VPN
        requireContext().applicationContext.startService(stopIntent)
    }

    private fun realStartVpn(server: ServerItem) {
        val context = requireContext().applicationContext
        val profile = importProfileFromOvpn(server)
        ProfileManager.getInstance(context).addProfile(profile)
        ProfileManager.saveProfile(context, profile)
        val intent = profile.getStartServiceIntent(context, "manual", true)
        context.startService(intent)
    }

    private fun importProfileFromOvpn(server: ServerItem): VpnProfile {
        val profile = VpnProfile(server.name)
        profile.mUseCustomConfig = true
        profile.mCustomConfigOptions = server.ovpn
        return profile
    }

    private fun cleanOldProfiles(context: Context) {
        val manager = ProfileManager.getInstance(context)
        val allProfiles = manager.profiles.toList()
        allProfiles.forEach { profile ->
            manager.removeProfile(context, profile)
        }
    }

    // ---- UI ----
    private fun fillViews(server: ServerItem) {
        textName.text = server.name
        textIp.text = "IP: ${server.ip ?: "-"}"
        textPort.text = "Port: ${server.port?.toString() ?: "-"}"
        textCountry.text = ServerFlag.getCountry(server.country)
        textPing.text = "Ping: ${server.ping?.toString() ?: "-"}"
        imageFlag.setImageResource(ServerFlag.getFlag(server.country))
        buttonConnectDisconnect.isEnabled = true
    }

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

    private fun updateVpnStatus() {
        if (isVpnConnected()) {
            buttonConnectDisconnect.text = getString(R.string.status_disconnect)
        } else {
            buttonConnectDisconnect.text = getString(R.string.status_connect)
        }
        buttonConnectDisconnect.isEnabled = currentServer != null
    }

    private fun isVpnConnected(): Boolean {
        return OpenVPNService.isConnected()
    }
}
