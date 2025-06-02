package fuck.system.vpn.servers.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class GetServersDialog : DialogFragment() {

    private val githubCsv =
        "https://raw.githubusercontent.com/rokkystudio/VPN/master/app/src/main/assets/vpngate.csv"
    private val googleCsv =
        "https://storage.googleapis.com/YOUR_BUCKET_NAME_HERE/vpngate.csv" // TODO: заменить

    private var cancelled = false
    private var currentServers: List<ServerItem> = emptyList()
    private var onResult: ((List<ServerItem>) -> Unit)? = null

    companion object {
        const val TAG = "GetServersDialog"

        fun newInstance(
            currentServers: List<ServerItem>,
            onResult: (List<ServerItem>) -> Unit
        ): GetServersDialog {
            val fragment = GetServersDialog()
            fragment.currentServers = currentServers
            fragment.onResult = onResult
            return fragment
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_get_servers, null, false)

        val textMessage = view.findViewById<TextView>(R.id.GetServersTextMessage)
        val cancelButton = view.findViewById<Button>(R.id.GetServersButtonCancel)

        textMessage.text = "Загружаем список серверов..."
        cancelButton.setOnClickListener {
            cancelled = true
            dismiss()
        }

        startLoading(textMessage)

        val dialog = Dialog(requireContext())
        dialog.setContentView(view)
        dialog.setCancelable(false)
        return dialog
    }

    private fun startLoading(textMessage: TextView) {
        thread {
            val servers = downloadAndParse(textMessage, githubCsv)
            if (servers.isNotEmpty()) {
                deliverResult(servers, success = true)
            } else {
                // Попытка скачать из Google Cloud
                val fallbackServers = downloadAndParse(textMessage, googleCsv)
                if (fallbackServers.isNotEmpty()) {
                    deliverResult(fallbackServers, success = true)
                } else {
                    // Обе попытки неудачны
                    if (isAdded && !cancelled) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Не удалось обновить список серверов!",
                                Toast.LENGTH_LONG
                            ).show()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun deliverResult(servers: List<ServerItem>, success: Boolean) {
        if (!cancelled && isAdded) {
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Список VPN серверов успешно обновлен.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val mergedServers = mergeFavorites(servers, currentServers)
                    onResult?.invoke(mergedServers)
                }
                dismiss()
            }
        }
    }

    private fun downloadAndParse(statusView: TextView, urlStr: String): List<ServerItem> {
        val servers = mutableListOf<ServerItem>()
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            if (connection.responseCode != 200) {
                Log.e(TAG, "Ошибка загрузки CSV: HTTP ${connection.responseCode}")
                return emptyList()
            }

            connection.inputStream.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    var isFirstLine = true
                    var count = 0
                    while (reader.readLine().also { line = it } != null) {
                        if (cancelled) break
                        if (isFirstLine) {
                            isFirstLine = false
                            continue
                        }

                        val parts = line?.split(",") ?: continue
                        if (parts.size < 15) continue

                        val ip = parts[1]
                        val country = parts[6].lowercase()
                        val ping = parts[3].toIntOrNull() ?: 999
                        val config = parts[14]

                        servers.add(
                            ServerItem(
                                ip = ip,
                                country = country,
                                ping = ping,
                                favorite = false,
                                openVpnConfigBase64 = config
                            )
                        )

                        count++
                        if (count % 50 == 0 && isAdded) {
                            val msg = "Загружено $count серверов..."
                            requireActivity().runOnUiThread {
                                statusView.text = msg
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке/парсинге CSV из $urlStr", e)
        }
        return servers
    }

    private fun mergeFavorites(
        newServers: List<ServerItem>,
        oldServers: List<ServerItem>
    ): List<ServerItem> {
        val favorites = oldServers.filter { it.favorite }.associateBy { it.ip }
        return newServers.map { server ->
            if (favorites.containsKey(server.ip)) server.copy(favorite = true) else server
        }
    }
}
