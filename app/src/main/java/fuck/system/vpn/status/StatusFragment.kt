package fuck.system.vpn.status

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.VpnProfile.EXTRA_PROFILEUUID
import de.blinkt.openvpn.VpnProfile.EXTRA_PROFILE_VERSION
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerGeo
import fuck.system.vpn.servers.server.ServerItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.StringReader
import java.util.UUID

/**
 * Фрагмент отображения статуса текущего VPN-соединения:
 * - Показывает информацию о сервере
 * - Позволяет подключаться и отключаться
 * - Запрашивает системное разрешение на использование VPN
 * - Отображает текущий ping (RTT), обновляемый в реальном времени
 */
class StatusFragment : Fragment(R.layout.fragment_status)
{
    /** Сервер, ожидающий подключения после завершения отключения текущего */
    private var pendingServer: ServerItem? = null

    /** Текущий отображаемый сервер */
    private var currentServer: ServerItem? = null

    /** Элементы управления и вывода информации */
    private lateinit var textCountry: TextView
    private lateinit var textIp: TextView
    private lateinit var textPort: TextView
    private lateinit var textPing: TextView
    private lateinit var textProto: TextView
    private lateinit var imageFlag: ImageView
    private lateinit var buttonAction: MaterialButton
    private lateinit var mapView: MapView

    /** Launcher для запроса системного разрешения на VPN */
    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>

    /** Обновление пинга в реальном времени */
    private var pingUpdateHandler: Handler? = null

    private val pingUpdateRunnable = object : Runnable {
        override fun run() {
            if (isVpnConnected()) {
                val ping = OpenVPNService.currentPing
                textPing.text = if (ping > 0) "${'$'}{ping}ms" else "-"
                pingUpdateHandler?.postDelayed(this, 1000)
            } else {
                textPing.text = "-"
            }
        }
    }

    private val vpnDisconnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                pendingServer?.let { server ->
                    realStartVpn(server)
                    pendingServer = null
                }
            }
        }
    }

    private val vpnStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mapView.update()
            updateVpnStatus()
        }
    }

    /**
     * Вызывается при создании представления.
     * Инициализирует UI, регистрирует VPN-события и настраивает кнопку подключения.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация UI-элементов
        textCountry = view.findViewById(R.id.StatusCountry)
        textIp = view.findViewById(R.id.StatusIp)
        textPort = view.findViewById(R.id.StatusPort)
        textPing = view.findViewById(R.id.StatusPing)
        textProto = view.findViewById(R.id.StatusProto)
        imageFlag = view.findViewById(R.id.StatusFlag)
        buttonAction = view.findViewById(R.id.StatusConnectDisconnect)
        mapView = view.findViewById(R.id.StatusMapView)
        mapView.update()

        // Регистрация BroadcastReceiver'ов
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

        // Запрос разрешения на VPN через launcher
        vpnPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                pendingServer?.let {
                    realStartVpn(it)
                    pendingServer = null
                }
            } else {
                Toast.makeText(requireContext(), "VPN permission denied", Toast.LENGTH_SHORT).show()
                pendingServer = null
            }
        }

        // Кнопка подключения/отключения
        buttonAction.setOnClickListener {
            currentServer?.let { server ->
                if (isVpnConnected()) stopVpn() else startVpn(server)
            }
        }
    }

    /**
     * Вызывается при возвращении на экран.
     * Загружает последний использованный сервер, обновляет UI и запускает пинг.
     */
    override fun onResume()
    {
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
     * Приостановка обновлений при уходе с экрана.
     */
    override fun onPause() {
        super.onPause()
        pingUpdateHandler?.removeCallbacks(pingUpdateRunnable)
        pingUpdateHandler = null
    }

    /**
     * Освобождает ресурсы и отключает подписки при уничтожении представления.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mapView.clear()
        requireContext().unregisterReceiver(vpnDisconnectedReceiver)
        requireContext().unregisterReceiver(vpnStatusReceiver)
    }

    /**
     * Запускает VPN.
     * Если требуется разрешение системы — запрашивает его, иначе сразу начинает подключение.
     */
    fun startVpn(server: ServerItem) {
        LastServerStorage.save(requireContext(), server)
        pendingServer = server

        val intent = VpnService.prepare(requireContext())
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            realStartVpn(server)
            pendingServer = null
        }
    }

    /**
     * Отправляет системный сигнал на отключение от текущего VPN.
     */
    fun stopVpn() {
        val stopIntent = Intent(requireContext(), OpenVPNService::class.java)
        stopIntent.action = OpenVPNService.DISCONNECT_VPN
        requireContext().applicationContext.startService(stopIntent)
    }

    /**
     * Производит непосредственное подключение к серверу,
     * импортируя профиль и инициируя подключение через OpenVPNService.
     */
    private fun realStartVpn(server: ServerItem)
    {
        val context = requireContext().applicationContext
        Log.e("VPN", "🚀 Начало realStartVpn для ${server.name}")

        val profile = importProfileFromOvpn(server)
        if (profile == null) {
            Log.e("VPN", "❌ Ошибка импорта профиля: null")
            Toast.makeText(context, "Ошибка импорта VPN‑профиля", Toast.LENGTH_SHORT).show()
            return
        }

        Log.e("VPN", "✅ Профиль импортирован: ${profile.name}, UUID: ${profile.uuidString}")

        cleanOldProfiles(context)
        Log.e("VPN", "🧹 Старые профили удалены")

        val manager = ProfileManager.getInstance(context)
        manager.addProfile(profile)
        Log.e("VPN", "📌 Профиль добавлен в менеджер")

        ProfileManager.saveProfile(context, profile)
        Log.e("VPN", "💾 Профиль сохранён")

        Log.e("VPN", "📂 filesDir при сохранении: ${context.filesDir.absolutePath}")

        ProfileManager.setConnectedVpnProfile(context, profile)
        Log.e("VPN", "🔗 Установлен активный профиль: ${profile.uuidString}")

        manager.saveProfileList(context) // 🔧 <-- ЭТО ОБЯЗАТЕЛЬНО!

        // 🔧 Форсируем сохранение чтобы было видно сразу в новом процессе
        val prefs = context.getSharedPreferences("de.blinkt.openvpn_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString("lastConnectedProfile", profile.uuidString).commit()

        // 🟡 Форсируем обновление
        manager.refreshVPNList(context)
        Log.e("VPN", "🔄 VPN список профилей обновлён. Всего: ${manager.profiles.size}")

        val confirm = ProfileManager.get(context, profile.uuidString, profile.mVersion, 20)
        if (confirm == null) {
            Log.e("VPN", "❌ Подтверждение профиля не удалось. UUID: ${profile.uuidString}")
            Toast.makeText(context, "VPN‑профиль не загружен", Toast.LENGTH_SHORT).show()
            return
        }

        Log.e("VPN", "✅ Профиль подтверждён: ${confirm.name}, UUID: ${confirm.uuidString}, версия: ${confirm.mVersion}")

        val intent = profile.getStartServiceIntent(context, "manual", true).apply {
            putExtra(EXTRA_PROFILEUUID, profile.uuidString)
            putExtra(EXTRA_PROFILE_VERSION, profile.mVersion)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    /**
     * Создаёт объект VPN-профиля из строки конфигурации .ovpn.
     */
    private fun importProfileFromOvpn(server: ServerItem): VpnProfile? {
        return try {
            val cp = ConfigParser()
            cp.parseConfig(StringReader(server.ovpn)) // парсим текст

            // ✅ создаём профиль, задаём имя и UUID
            cp.convertProfile().apply {
                mName = server.name
                setUUID(UUID.randomUUID())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Удаляет все сохранённые VPN-профили перед созданием нового.
     */
    private fun cleanOldProfiles(context: Context) {
        val manager = ProfileManager.getInstance(context)
        val allProfiles = manager.profiles.toList()
        allProfiles.forEach { manager.removeProfile(context, it) }
    }

    /**
     * Заполняет интерфейс данными о текущем сервере.
     */
    private fun fillViews(server: ServerItem) {
        textCountry.text = ServerGeo.getCountry(server.country)
        textIp.text = server.ip
        textPort.text = ":" + (server.port?.toString() ?: "-")
        textProto.text = server.proto
        textPing.text = server.ping?.toString() ?: "-"
        imageFlag.setImageResource(ServerGeo.getFlag(server.country))
        buttonAction.isEnabled = true
    }

    /**
     * Очищает UI и блокирует кнопку действия, если сервер не выбран.
     */
    private fun clearViews() {
        textCountry.text = ""
        textIp.text = ""
        textPort.text = ""
        textProto.text = ""
        textPing.text = ""
        imageFlag.setImageDrawable(null)
        buttonAction.text = getString(R.string.connect)
        buttonAction.isEnabled = false
    }

    /**
     * Обновляет надпись и доступность кнопки подключения в зависимости от текущего состояния VPN.
     */
    private fun updateVpnStatus()
    {
        if (isVpnConnected()) {
            buttonAction.text = getString(R.string.disconnect)
        } else {
            buttonAction.text = getString(R.string.connect)
        }

        buttonAction.isEnabled = currentServer != null
    }

    /**
     * Проверяет активность VPN-подключения через OpenVPNService.
     */
    private fun isVpnConnected(): Boolean = OpenVPNService.isConnected()
}
