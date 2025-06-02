package fuck.system.vpn.servers.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class PingServersDialog(
    private val servers: List<ServerItem>,
    private val onComplete: () -> Unit
) : DialogFragment() {

    private val threads = 4

    @Volatile
    private var cancelled = false
    private val threadPool = Executors.newFixedThreadPool(threads)

    companion object {
        const val TAG = "CountryFilterDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ping_servers)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val progressBar = dialog.findViewById<ProgressBar>(R.id.ServersPingProgress)
        val progressText = dialog.findViewById<TextView>(R.id.ServersPingState)
        val cancelButton = dialog.findViewById<Button>(R.id.ServersAddCancel)

        progressBar?.max = servers.size
        progressBar?.progress = 0
        progressText?.text = getString(R.string.servers_ping_state, 0, servers.size)

        cancelButton?.setOnClickListener {
            cancelled = true
            dismissAllowingStateLoss()
        }

        startPingThreads(progressBar, progressText)
    }

    private fun startPingThreads(
        progressBar: ProgressBar?,
        progressText: TextView?
    ) {
        val total = servers.size
        var completed = 0
        val lock = Object()

        if (servers.isEmpty()) {
            dismissAllowingStateLoss()
            return
        }

        servers.forEach { server ->
            threadPool.submit {
                if (cancelled) {
                    server.ping = null
                    return@submit
                }

                server.ping = if (server.ip != null && server.port != null)
                    tcpCheck(server.ip, server.port, 1000)
                else
                    null

                synchronized(lock) {
                    completed++
                    activity?.runOnUiThread {
                        progressBar?.progress = completed
                        progressText?.text =
                            getString(R.string.servers_ping_state, completed, total)
                    }
                }
            }
        }

        thread {
            threadPool.shutdown()
            threadPool.awaitTermination((servers.size * 2).toLong(), TimeUnit.SECONDS)
            if (!cancelled) {
                activity?.runOnUiThread {
                    dismissAllowingStateLoss()
                    onComplete()
                }
            }
        }
    }

    private fun tcpCheck(ip: String, port: Int, timeoutMs: Int): Int? {
        val start = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
            }
            return (System.currentTimeMillis() - start).toInt()
        } catch (e: Exception) {
            return null
        }
    }

    override fun onDestroyView() {
        cancelled = true
        threadPool.shutdownNow()
        super.onDestroyView()
    }
}
