package fuck.system.vpn.getservers

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.serverlist.ServerListItem
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class GetServersDialog : DialogFragment()
{

    private var cancelled = false
    private var currentServers: List<ServerListItem> = emptyList()
    private var onResult: ((List<ServerListItem>) -> Unit)? = null

    companion object {
        const val TAG = "GetServersDialog"

        fun newInstance(
            currentServers: List<ServerListItem>,
            onResult: (List<ServerListItem>) -> Unit
        ): GetServersDialog {
            val fragment = GetServersDialog()
            fragment.currentServers = currentServers
            fragment.onResult = onResult
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Первый диалог с подтверждением
        return AlertDialog.Builder(requireContext())
            .setTitle("Загрузка списка серверов")
            .setMessage("Старый список серверов будет очищен. Избранные серверы не будут удалены. Продолжить?")
            .setPositiveButton("Да") { _, _ ->
                showLoadingDialogAndStart()
            }
            .setNegativeButton("Отмена") { _, _ ->
                dismiss()
            }
            .create()
    }

    private fun showLoadingDialogAndStart() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_get_servers, null)
        val textMessage = view.findViewById<TextView>(R.id.textDialogMessage)
        textMessage.text = "Загружаем список серверов с сайта VPNGate..."

        val loadingDialog = AlertDialog.Builder(requireContext())
            .setTitle("Загрузка серверов")
            .setView(view)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ ->
                cancelled = true
                dismiss()
            }
            .create()

        loadingDialog.show()

        thread {
            val servers = downloadAndParse()
            if (!cancelled) {
                requireActivity().runOnUiThread {
                    loadingDialog.dismiss()
                    val mergedServers = mergeFavorites(servers, currentServers)
                    onResult?.invoke(mergedServers)
                    dismiss()
                }
            } else {
                requireActivity().runOnUiThread {
                    loadingDialog.dismiss()
                    dismiss()
                }
            }
        }
    }

    private fun downloadAndParse(): List<ServerListItem> {
        val servers = mutableListOf<ServerListItem>()
        try {
            val url = URL("https://www.vpngate.net/api/iphone/")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 10000

            connection.inputStream.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    var count = 0
                    while (reader.readLine().also { line = it } != null) {
                        if (cancelled) break
                        count++
                        if (count <= 2 || line.isNullOrBlank()) continue

                        val parts = line!!.split(",")
                        if (parts.size < 15) continue

                        val ip = parts[1]
                        val country = parts[6].lowercase()
                        val ping = parts[3].toIntOrNull() ?: 999
                        val config = parts[14]

                        servers.add(
                            ServerListItem(
                                ip = ip,
                                country = country,
                                ping = ping,
                                favorite = false,
                                openVpnConfigBase64 = config
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return servers
    }

    private fun mergeFavorites(newServers: List<ServerListItem>, oldServers: List<ServerListItem>): List<ServerListItem> {
        val favorites = oldServers.filter { it.favorite }.associateBy { it.ip }
        return newServers.map { server ->
            if (favorites.containsKey(server.ip)) server.copy(favorite = true) else server
        }
    }
}