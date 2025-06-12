package fuck.system.vpn.servers.ping

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Messenger
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServersStorage
import kotlin.math.min

/**
 * Диалоговое окно для выполнения массового пинга VPN-серверов.
 *
 * Компонент запускает фоновый сервис PingService, который отправляет UDP/TCP-пакеты
 * на список серверов, полученный из локального хранилища (ServersStorage),
 * и отображает прогресс пинга с возможностью отмены.
 *
 * По завершении диалог сохраняет обновлённый список серверов с результатами RTT
 * обратно в хранилище и возвращает их через FragmentResult.
 */
class ServersPingDialog : DialogFragment()
{
    override fun getTheme(): Int = R.style.DialogTheme

    companion object {
        const val TAG = "PingServersDialog"
    }

    private lateinit var pingServiceIntent: Intent

    // UI-элементы
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var cancelButton: Button

    // Прогресс
    private var completed = 0
    private var cancelled = false
    private var hasStarted = false

    // Текущий список серверов для пинга
    private var servers: ArrayList<ServerItem> = arrayListOf()

    /**
     * Handler для приёма результатов пинга от PingService через Messenger.
     * Обновляет RTT у соответствующего ServerItem и вызывает обновление прогресса.
     */
    private val pingHandler = Handler(Looper.getMainLooper()) { msg ->
        val ip = msg.data.getString("ip") ?: return@Handler true
        val ping = if (msg.data.containsKey("ping")) msg.data.getInt("ping") else null

        if (!cancelled) {
            servers.find { it.ip == ip }?.ping = ping
            updateProgress()
        }
        true
    }

    /** Messenger, который передаётся в PingService для получения результатов */
    private val pingMessenger = Messenger(pingHandler)

    /** Launcher для получения разрешения от пользователя на использование VPN. */
    private val vpnPrepareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startPingService() // <- запускаем после получения разрешения
        } else {
            Toast.makeText(requireContext(), "Разрешение на VPN не получено", Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
        }
    }

    /**
     * Загружает layout диалога.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_servers_ping, container, false)
    }

    /**
     * Прерывает процесс пинга и останавливает PingService,
     * если пользователь закрыл диалог или нажал кнопку "Отмена".
     */
    override fun onDestroyView() {
        cancelled = true
        stopPingService()
        super.onDestroyView()
    }

    /**
     * Инициализирует UI и запускает процесс пинга.
     * Защищается от повторного запуска.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.ServersPingProgress)
        progressText = view.findViewById(R.id.ServersPingState)
        cancelButton = view.findViewById(R.id.ServersAddCancel)

        cancelButton.setOnClickListener {
            cancelled = true
            stopPingService()
            dismissAllowingStateLoss()
        }

        if (!hasStarted) {
            hasStarted = true
            startPinging()
        }
    }

    /**
     * Загружает список серверов из хранилища и передаёт их в PingService
     * для выполнения TCP/UDP-пинга. Передаёт Messenger для получения результатов.
     */
    private fun startPinging() {
        if (!isAdded) return

        val intent = VpnService.prepare(requireContext())
        if (intent != null) {
            vpnPrepareLauncher.launch(intent)
        } else {
            startPingService()
        }
    }

    /**
     * Завершает пинг:
     * - сохраняет обновлённый список серверов в хранилище,
     * - останавливает PingService,
     * - закрывает диалог.
     */
    private fun finishPinging() {
        ServersStorage.save(requireContext(), servers)
        stopPingService()
        dismissAllowingStateLoss()
    }

    /**
     * Подготавливает и запускает PingService:
     * - загружает список серверов из локального хранилища,
     * - инициализирует прогрессбар и текст состояния,
     * - формирует Intent с Messenger'ом для обратной связи,
     * - запускает сервис для выполнения TCP/UDP-пинга.
     */
    private fun startPingService()
    {
        servers = ArrayList(ServersStorage.load(requireContext()))
        completed = 0

        progressBar.max = servers.size
        progressText.text = getString(R.string.servers_ping_state, 0, servers.size)

        pingServiceIntent = Intent(requireContext(), ServersPingService::class.java).apply {
            putExtra("messenger", pingMessenger)
        }

        requireContext().startService(pingServiceIntent)
    }

    /**
     * Останавливает PingService, если он был запущен.
     */
    private fun stopPingService() {
        requireContext().stopService(pingServiceIntent)
    }

    /**
     * Обновляет состояние прогрессбара после получения очередного ответа.
     * Когда все ответы получены:
     *  - сохраняет обновлённый список обратно в хранилище,
     *  - возвращает его вызывающему фрагменту,
     *  - закрывает диалог.
     */
    private fun updateProgress()
    {
        if (view == null) return

        completed = min(completed + 1, servers.size)

        progressBar.progress = completed
        progressText.text = getString(R.string.servers_ping_state, completed, servers.size)

        if (completed >= servers.size && !cancelled && isAdded) {
            finishPinging()
        }
    }
}