package fuck.system.vpn.serverlist

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import fuck.system.vpn.R
import kotlin.concurrent.thread

class PingServersDialog(
    private val context: Context,
    private val servers: List<ServerListItem>,
    private val onComplete: () -> Unit
) {
    private var dialog: AlertDialog? = null
    @Volatile
    private var cancelled = false

    fun start() {
        cancelled = false
        initDialog()
        startPingThread()
    }

    private fun initDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_ping_servers, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = view.findViewById<TextView>(R.id.progressText)

        progressBar.max = servers.size
        progressBar.progress = 0
        progressText.text = "Ping servers: 0/${servers.size}"

        dialog = AlertDialog.Builder(context)
            .setTitle("Пинг серверов")
            .setView(view)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ -> cancelled = true }
            .create()

        dialog?.show()
    }

    private fun startPingThread()
    {
        val progressBar = dialog?.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = dialog?.findViewById<TextView>(R.id.progressText)

        thread {
            servers.forEachIndexed { index, server ->
                if (cancelled) {
                    dismissDialog()
                    return@thread
                }

                server.ping = ping(server.ip)

                (context as? Activity)?.runOnUiThread {
                    progressBar?.progress = index + 1
                    progressText?.text = "Ping servers: ${index + 1}/${servers.size}"
                }
            }

            if (!cancelled) {
                (context as? Activity)?.runOnUiThread {
                    dismissDialog()
                    onComplete()
                }
            }
        }
    }

    private fun dismissDialog() {
        (context as? Activity)?.runOnUiThread {
            dialog?.dismiss()
        }
    }

    private fun ping(ip: String): Int {
        var ping = 999
        try {
            val process = ProcessBuilder("ping", "-c", "1", "-W", "1", ip)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val match = Regex("time=([0-9.]+)").find(output)
            val time = match?.groupValues?.get(1)?.toFloatOrNull()

            time?.toInt()?.let { ping = it }
        } catch (e: Exception) {
            Log.e("Ping", "Ошибка пинга $ip", e)
        }
        return ping
    }
}