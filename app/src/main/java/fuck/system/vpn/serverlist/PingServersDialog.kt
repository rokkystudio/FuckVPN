package fuck.system.vpn.serverlist

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import fuck.system.vpn.R
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class PingServersDialog(
    private val context: Context,
    private val servers: List<ServerListItem>,
    private val onComplete: () -> Unit
) {
    private var dialog: AlertDialog? = null
    @Volatile
    private var cancelled = false

    private val threadPool = Executors.newFixedThreadPool(4)  // 4 параллельных потока

    fun start() {
        cancelled = false
        initDialog()
        startPingThreads()
    }

    private fun initDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_ping_servers, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = view.findViewById<TextView>(R.id.progressText)

        progressBar.max = servers.size
        progressBar.progress = 0
        progressText.text = "Проверка серверов: 0/${servers.size}"

        dialog = AlertDialog.Builder(context)
            .setTitle("Проверка доступности серверов")
            .setView(view)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ -> cancelled = true }
            .create()

        dialog?.show()
    }

    private fun startPingThreads() {
        val progressBar = dialog?.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = dialog?.findViewById<TextView>(R.id.progressText)

        thread {
            val total = servers.size
            var completed = 0

            val lock = Object()

            servers.forEach { server ->
                threadPool.submit {
                    if (cancelled) return@submit

                    // Проверка доступности сервера
                    server.ping = tcpCheck(server.ip, server.port, 1000)

                    synchronized(lock) {
                        completed++
                        (context as? Activity)?.runOnUiThread {
                            progressBar?.progress = completed
                            progressText?.text = "Проверка серверов: $completed/$total"
                        }
                    }
                }
            }

            threadPool.shutdown()
            threadPool.awaitTermination(servers.size.toLong() * 2, TimeUnit.SECONDS)

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

    /**
     * Проверка доступности OpenVPN сервера по TCP
     * Возвращает время в мс или 999 при ошибке
     */
    private fun tcpCheck(ip: String, port: Int, timeoutMs: Int): Int {
        val start = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
            }
            return (System.currentTimeMillis() - start).toInt()
        } catch (e: Exception) {
            Log.w("PingServersDialog", "Не удалось подключиться к $ip:$port", e)
            return 999
        }
    }
}
