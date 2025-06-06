package fuck.system.vpn.servers.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import fuck.system.vpn.R
import fuck.system.vpn.parser.ServersParser
import fuck.system.vpn.servers.server.ServersStorage
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetServersDialogAlert(private val context: Context, private val lifecycleOwner: LifecycleOwner) {

    companion object {
        private const val githubCsv = "https://raw.githubusercontent.com/rokkystudio/VPN/master/app/src/main/assets/vpngate.csv"
        private const val assetCsv = "vpngate.csv"
    }

    private var dialog: AlertDialog? = null
    private var job: Job? = null
    private var cancelled = false

    fun show() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_get_servers, null)

        val textMessage = view.findViewById<TextView>(R.id.GetServersTextMessage)
        val progressBar = view.findViewById<ProgressBar>(R.id.GetServersProgressBar)
        val percentView = view.findViewById<TextView>(R.id.GetServersPercentText)
        val closeButton = view.findViewById<View>(R.id.GetServersCloseButton)

        val builder = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(view)

        dialog = builder.create()
        dialog?.show()

        closeButton.setOnClickListener {
            cancelled = true
            dialog?.dismiss()
        }

        // Запускаем загрузку серверов с безопасной корутиной
        job = lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val csv = downloadUrl(githubCsv, progressBar, percentView)

            withContext(Dispatchers.Main) {
                if (cancelled) return@withContext
                if (csv != null) {
                    onSuccess(csv)
                } else {
                    onFailure()
                }
                dialog?.dismiss()
            }
        }
    }

    private fun onSuccess(csv: String) {
        val count = parseAndSaveCsv(csv)
        Toast.makeText(context, context.getString(R.string.servers_updated, count), Toast.LENGTH_SHORT).show()
    }

    private fun onFailure() {
        Toast.makeText(context, context.getString(R.string.servers_update_failed), Toast.LENGTH_SHORT).show()
        val existing = ServersStorage.load(context)
        if (existing.isNotEmpty()) return

        try {
            val input = context.assets.open(assetCsv)
            val reader = BufferedReader(InputStreamReader(input))
            val parsed = ServersParser.parseCsv(reader)
            ServersStorage.save(context, parsed)
            Toast.makeText(context, context.getString(R.string.servers_loaded_from_assets, parsed.size), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.servers_assets_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun parseAndSaveCsv(csv: String): Int {
        val current = ServersStorage.load(context)
        val favorites = current.filter { it.favorite }

        val reader = csv.reader().buffered()
        val parsed = ServersParser.parseCsv(reader)

        val resultMap = favorites.associateBy { it.ip }.toMutableMap()
        for (server in parsed) {
            if (!resultMap.containsKey(server.ip)) {
                resultMap[server.ip] = server
            }
        }

        ServersStorage.save(context, resultMap.values.toList())
        return resultMap.size
    }

    private suspend fun downloadUrl(urlStr: String, progressBar: ProgressBar, percentView: TextView): String? = withContext(Dispatchers.IO) {
        var result: String? = null

        try {
            val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (connection.responseCode == 200) {
                connection.inputStream.use { inputStream ->
                    result = readStream(inputStream, connection.contentLength, progressBar, percentView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext result
    }

    private fun readStream(
        inputStream: java.io.InputStream,
        totalLength: Int,
        progressBar: ProgressBar,
        percentView: TextView
    ): String {
        val buffer = ByteArray(4096)
        val out = StringBuilder()
        var totalRead = 0
        var read: Int

        // Примерный размер CSV-файла в байтах (можно подкорректировать)
        val estimatedSize = 1024*1024
        val expectedLength = if (totalLength <= 0) estimatedSize else totalLength

        var lastProgress = -1

        while (inputStream.read(buffer).also { read = it } != -1 && !cancelled) {
            out.append(String(buffer, 0, read))
            totalRead += read

            val progress = (totalRead * 100) / expectedLength
            if (progress != lastProgress && progress <= 100) {
                lastProgress = progress
                lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    progressBar.progress = progress
                    percentView.text = context.getString(R.string.servers_get_loading_percent, progress)
                }
            }
        }

        // Завершаем на 100%
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            progressBar.progress = 100
            percentView.text = context.getString(R.string.servers_get_loading_percent, 100)
        }

        return out.toString()
    }

    fun dismiss() {
        cancelled = true
        job?.cancel()
        dialog?.dismiss()
    }
}
