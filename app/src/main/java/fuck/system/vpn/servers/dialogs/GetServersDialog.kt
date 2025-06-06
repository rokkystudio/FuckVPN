package fuck.system.vpn.servers.dialogs

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import fuck.system.vpn.R
import fuck.system.vpn.parser.ServersParser
import fuck.system.vpn.servers.server.ServersStorage
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetServersDialog : DialogFragment() {

    companion object {
        const val TAG = "GetServersDialog"
        private const val githubCsv = "https://raw.githubusercontent.com/rokkystudio/VPN/master/app/src/main/assets/vpngate.csv"
        private const val assetCsv = "vpngate.csv"
        private const val estimatedSize = 1024 * 1024 // Примерный размер файла в байтах
    }

    private lateinit var textMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var percentView: TextView
    private lateinit var closeButton: Button

    @Volatile private var cancelled = false
    private var job: Job? = null
    private var hasStarted = false

    /**
     * Подключает layout-ресурс
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_get_servers, container, false)
    }

    /**
     * Инициализирует элементы интерфейса и запускает загрузку серверов
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textMessage = view.findViewById(R.id.GetServersTextMessage)
        progressBar = view.findViewById(R.id.GetServersProgressBar)
        percentView = view.findViewById(R.id.GetServersPercentText)
        closeButton = view.findViewById(R.id.GetServersCloseButton)

        closeButton.setOnClickListener {
            cancelled = true
            dismissAllowingStateLoss()
        }

        if (!hasStarted) {
            hasStarted = true
            startLoading()
        }
    }

    /**
     * Запускает фоновую корутину для загрузки серверов из сети или fallback из assets
     */
    private fun startLoading() {
        progressBar.progress = 0
        percentView.text = ""
        textMessage.setText(R.string.servers_get_loading)

        val context = requireContext()

        job = requireActivity().lifecycleScope.launch(Dispatchers.IO) {
            val csv = downloadUrl(githubCsv)

            withContext(Dispatchers.Main) {
                if (!isAdded || cancelled) return@withContext

                if (csv != null) {
                    onSuccess(csv, context)
                } else {
                    onFailure(context)
                }
            }
        }
    }

    /**
     * Обрабатывает успешную загрузку CSV, парсит и сохраняет список
     */
    private fun onSuccess(csv: String, context: Context) {
        val count = parseAndSaveCsv(csv)
        Toast.makeText(context, getString(R.string.servers_updated, count), Toast.LENGTH_SHORT).show()
        dismissAllowingStateLoss()
    }

    /**
     * Обрабатывает неудачную загрузку: пробует загрузить из кэша или из assets
     */
    private fun onFailure(context: Context) {
        Toast.makeText(context, getString(R.string.servers_update_failed), Toast.LENGTH_SHORT).show()

        val existing = ServersStorage.load(context)
        if (existing.isNotEmpty()) {
            dismissAllowingStateLoss()
            return
        }

        try {
            val input = context.assets.open(assetCsv)
            val reader = BufferedReader(InputStreamReader(input))
            val parsed = ServersParser.parseCsv(reader)
            ServersStorage.save(context, parsed)
            Toast.makeText(context, getString(R.string.servers_loaded_from_assets, parsed.size), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.servers_assets_error, e.message), Toast.LENGTH_LONG).show()
        } finally {
            dismissAllowingStateLoss()
        }
    }

    /**
     * Парсит CSV, сохраняет, при этом восстанавливает избранные из старого списка
     * @return Кол-во серверов после объединения
     */
    private fun parseAndSaveCsv(csv: String): Int {
        val context = requireContext()
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

    /**
     * Загружает CSV-файл с серверами по URL, если удачно — возвращает содержимое
     */
    private suspend fun downloadUrl(urlStr: String): String? = withContext(Dispatchers.IO) {
        var result: String? = null

        try {
            val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (connection.responseCode == 200) {
                connection.inputStream.use { inputStream ->
                    val length = if (connection.contentLength > 0) connection.contentLength else estimatedSize
                    result = readStream(inputStream, length)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext result
    }

    /**
     * Читает входной поток как строку, отображает прогресс
     */
    private fun readStream(inputStream: java.io.InputStream, totalLength: Int): String {
        val buffer = ByteArray(4096)
        val out = StringBuilder()
        var totalRead = 0
        var read: Int
        var lastProgress = -1

        while (inputStream.read(buffer).also { read = it } != -1 && !cancelled) {
            out.append(String(buffer, 0, read))
            totalRead += read

            val progress = (totalRead * 100) / totalLength
            if (progress != lastProgress && progress <= 100) {
                lastProgress = progress
                lifecycleScope.launch(Dispatchers.Main) {
                    progressBar.progress = progress
                    percentView.text = getString(R.string.servers_get_loading_percent, progress)
                }
            }
        }

        // Завершаем на 100%
        lifecycleScope.launch(Dispatchers.Main) {
            progressBar.progress = 100
            percentView.text = getString(R.string.servers_get_loading_percent, 100)
        }

        return out.toString()
    }

    /**
     * Отмена загрузки и очистка ресурсов при уничтожении view
     */
    override fun onDestroyView() {
        cancelled = true
        job?.cancel()
        super.onDestroyView()
    }
}
