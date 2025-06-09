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
import fuck.system.vpn.servers.server.ServerStorage
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
class PingDialog : DialogFragment()
{
    companion object {
        /** Тэг для отображения диалога через FragmentManager */
        const val TAG = "PingServersDialog"

        /** Ключ результата для передачи обновлённого списка серверов */
        const val RESULT_KEY = "PingServersDialogResult"

        /** Имя поля в Bundle, где передаются ServerItem-ы */
        const val RESULT_EXTRA = "SERVERS"
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
        return inflater.inflate(R.layout.dialog_ping, container, false)
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
            requireContext().stopService(Intent(requireContext(), PingService::class.java))
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

    private fun startPingService() {
        servers = ArrayList(ServerStorage.load(requireContext()))
        completed = 0

        progressBar.max = servers.size
        progressText.text = getString(R.string.ping_state, 0, servers.size)

        pingServiceIntent = Intent(requireContext(), PingService::class.java).apply {
            putExtra("messenger", pingMessenger)
        }

        requireContext().startService(pingServiceIntent)
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
        progressText.text = getString(R.string.ping_state, completed, servers.size)

        if (completed >= servers.size && !cancelled && isAdded) {
            ServerStorage.save(requireContext(), servers)

            parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle().apply {
                putParcelableArrayList(RESULT_EXTRA, servers)
            })

            requireContext().stopService(pingServiceIntent)
            dismissAllowingStateLoss()
        }
    }

    /**
     * Прерывает процесс пинга и останавливает PingService,
     * если пользователь закрыл диалог или нажал кнопку "Отмена".
     */
    override fun onDestroyView() {
        cancelled = true
        requireContext().stopService(pingServiceIntent)
        super.onDestroyView()
    }
}