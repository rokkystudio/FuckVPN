package fuck.system.vpn.servers.dialogs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.status.PingObserver
import kotlinx.coroutines.*
import kotlin.math.min

class PingServersDialog : DialogFragment() {

    companion object {
        const val TAG = "PingServersDialog"
        private const val SERVERS_KEY = "SERVERS"
        const val RESULT_KEY = "PingServersDialogResult"
        const val RESULT_EXTRA = "SERVERS"

        fun newInstance(servers: List<ServerItem>): PingServersDialog {
            val fragment = PingServersDialog()
            val args = Bundle()
            args.putParcelableArrayList(SERVERS_KEY, ArrayList(servers))
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var cancelButton: Button

    private val observers = mutableListOf<PingObserver>()
    private var job: Job? = null
    private var completed = 0
    private var cancelled = false

    private var servers: ArrayList<ServerItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        servers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(SERVERS_KEY, ServerItem::class.java) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(SERVERS_KEY) ?: arrayListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_ping_servers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.ServersPingProgress)
        progressText = view.findViewById(R.id.ServersPingState)
        cancelButton = view.findViewById(R.id.ServersAddCancel)

        progressBar.max = servers.size
        progressText.text = getString(R.string.servers_ping_state, 0, servers.size)

        cancelButton.setOnClickListener {
            cancelled = true
            dismissAllowingStateLoss()
        }

        startPinging()
    }

    private fun startPinging() {
        if (!isAdded) return

        completed = 0
        observers.clear()

        job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                servers.forEach { server ->
                    val ip = server.ip ?: return@forEach
                    val observer = PingObserver(ip)
                    observers.add(observer)

                    observer.observe(viewLifecycleOwner) { result ->
                        if (!cancelled) {
                            server.ping = result.toIntOrNull()
                            updateProgress()
                        }
                    }

                    observer.start(viewLifecycleOwner.lifecycleScope)
                    delay(50)
                }
            }
        }
    }

    private fun updateProgress() {
        if (view == null) return

        completed = min(completed + 1, servers.size)

        progressBar.progress = completed
        progressText.text = getString(R.string.servers_ping_state, completed, servers.size)

        if (completed >= servers.size && !cancelled && isAdded) {
            parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle().apply {
                putParcelableArrayList(RESULT_EXTRA, servers)
            })
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        cancelled = true
        job?.cancel()
        observers.forEach { it.stop() }
        observers.clear()
        super.onDestroyView()
    }
}

val Bundle.pingServers: ArrayList<ServerItem>?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableArrayList(PingServersDialog.RESULT_EXTRA, ServerItem::class.java)
    else
        @Suppress("DEPRECATION") getParcelableArrayList(PingServersDialog.RESULT_EXTRA)
