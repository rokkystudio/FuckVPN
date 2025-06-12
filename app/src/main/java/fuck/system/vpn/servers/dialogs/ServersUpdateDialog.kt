package fuck.system.vpn.servers.dialogs

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.parser.ServersParser
import fuck.system.vpn.servers.server.ServersStorage
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.edit
import fuck.system.vpn.servers.server.ServerItem

/**
 * Диалог обновления списка VPN-серверов.
 *
 * Пытается загрузить CSV-файл с серверами из интернета. Если не удаётся —
 * использует локальный кэш. Если кэша нет — использует файл из assets.
 * Сохраняет результат и сохраняет избранные сервера из предыдущего списка.
 */
class ServersUpdateDialog : DialogFragment()
{
    override fun getTheme(): Int = R.style.DialogTheme

    companion object
    {
        const val TAG = "GetServersDialog"

        private const val CACHE_PREF = "vpn_cache"
        private const val KEY_LAST_CSV = "last_csv"

        private const val GITHUB_CSV_URL = "https://raw.githubusercontent.com/rokkystudio/VPN/master/app/src/main/assets/vpngate.csv"
        private const val ASSET_CSV_NAME = "vpngate.csv"

        private const val CONNECT_TIMEOUT_MS = 3000
        private const val READ_TIMEOUT_MS = 3000
        private const val HTTP_SUCCESS_CODE = 200
        private const val ESTIMATED_SIZE_BYTES = 1258316 // Примерный размер файла в байтах
        private const val BUFFER_SIZE = 512
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var percentView: TextView
    private lateinit var closeButton: Button

    private var hasStarted = false

    /**
     * Подключает layout-ресурс
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_servers_update, container, false)
    }

    /**
     * Инициализирует элементы интерфейса и запускает загрузку серверов
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.ServersUpdateProgress)
        percentView = view.findViewById(R.id.ServersUpdateState)
        closeButton = view.findViewById(R.id.ServersUpdateClose)

        closeButton.setOnClickListener {
            dismissAllowingStateLoss()
        }

        if (!hasStarted) {
            hasStarted = true
            startLoading()
        }
    }

    /**
     * Отмена загрузки и очистка ресурсов при уничтожении view
     */
    override fun onDestroyView() {
        hasStarted = false
        super.onDestroyView()
    }

    /**
     * Запускает загрузку серверов в отдельном потоке.
     */
    private fun startLoading() {
        Thread {
            performLoading()
        }.start()
    }

    /**
     * Выполняет загрузку CSV и передаёт результат в основной поток.
     */
    private fun performLoading()
    {
        if (!isAdded || !hasStarted) return

        val context = requireContext()
        val csv = downloadCsv(GITHUB_CSV_URL)

        if (!isAdded || !hasStarted) return

        if (csv != null) {
            onSuccess(context, csv)
        } else {
            onFailure(context)
        }
    }

    /**
     * Обрабатывает успешную загрузку CSV: сохраняет его в кэш,
     * парсит, объединяет с избранными и сохраняет в хранилище.
     */
    private fun onSuccess(context: Context, csv: String)
    {
        saveCsvCache(context, csv)
        val count = parseCsvString(csv)

        runOnUiThreadSafe {
            showToast(context, context.getString(R.string.server_update_complete, count))
            dismissAllowingStateLoss()
        }
    }

    /**
     * Обрабатывает неудачную загрузку:
     * пробует восстановить список из кэша или из assets.
     */
    private fun onFailure(context: Context)
    {
        // Сначала пытаемся загрузить из кэша
        val cacheCount = parseCsvCache(context)
        if (cacheCount != null) {
            runOnUiThreadSafe {
                showToast(context, context.getString(R.string.server_update_from_cache, cacheCount))
                dismissAllowingStateLoss()
            }
            return
        }

        // Если кэш не сработал — пробуем из assets
        val assetCount = parseCsvAssets(context)
        if (assetCount != null) {
            runOnUiThreadSafe {
                showToast(context, context.getString(R.string.server_update_from_assets, assetCount))
                dismissAllowingStateLoss()
            }
            return
        }

        // Всё провалилось — показываем ошибку
        runOnUiThreadSafe {
            showToast(context, context.getString(R.string.server_update_failed))
            dismissAllowingStateLoss()
        }
    }

    /**
     * Парсит CSV-строку, объединяет с избранными и сохраняет.
     * @return Общее количество серверов после объединения.
     */
    private fun parseCsvString(csv: String): Int {
        val context = requireContext()
        val reader = csv.reader().buffered()
        val parsed = ServersParser.parseCsv(reader)
        return updateServers(context, parsed)
    }

    /**
     * Загружает список из кэша и сохраняет.
     * @return Количество серверов или null при ошибке.
     */
    private fun parseCsvCache(context: Context): Int? {
        return try {
            val csv = context.getSharedPreferences(CACHE_PREF, Context.MODE_PRIVATE)
                .getString(KEY_LAST_CSV, null) ?: return null
            parseCsvString(csv)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Загружает список из assets и сохраняет.
     * @return Количество серверов или null при ошибке.
     */
    private fun parseCsvAssets(context: Context): Int? {
        return try {
            val input = context.assets.open(ASSET_CSV_NAME)
            val csv = input.bufferedReader().readText()
            parseCsvString(csv)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Объединяет новый список с текущими избранными серверами и сохраняет.
     * @return Общее количество серверов после объединения.
     */
    private fun updateServers(context: Context, parsed: List<ServerItem>): Int
    {
        val current = ServersStorage.load(context)
        val favorites = current.filter { it.favorite }

        val resultMap = favorites.associateBy { it.ip }.toMutableMap()
        parsed.forEach { server ->
            resultMap.putIfAbsent(server.ip, server)
        }

        ServersStorage.save(context, resultMap.values.toList())
        return resultMap.size
    }

    /**
     * Сохраняет CSV в SharedPreferences как последний успешный сеанс.
     */
    private fun saveCsvCache(context: Context, csv: String) {
        context.getSharedPreferences(CACHE_PREF, Context.MODE_PRIVATE).edit {
            putString(KEY_LAST_CSV, csv)
        }
    }

    /**
     * Загружает CSV-файл с серверами по URL, если удачно — возвращает содержимое
     */
    @Suppress("SameParameterValue")
    private fun downloadCsv(urlStr: String): String?
    {
        try {
            val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
            }

            if (hasStarted && connection.responseCode == HTTP_SUCCESS_CODE)
            {
                val input = connection.inputStream
                var length = ESTIMATED_SIZE_BYTES

                if (connection.contentLength > 0) {
                    length = connection.contentLength
                }
                return input.use { readStream(it, length) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Читает входной поток как строку, отображает прогресс
     */
    private fun readStream(inputStream: java.io.InputStream, totalLength: Int): String
    {
        val buffer = ByteArray(BUFFER_SIZE)
        val out = StringBuilder()
        var totalRead = 0
        var read: Int

        while (inputStream.read(buffer).also { read = it } != -1 && hasStarted) {
            out.append(String(buffer, 0, read))
            totalRead += read
            updateProgress(totalRead, totalLength)
        }

        finishProgress()
        return out.toString()
    }

    /**
     * Обновляет прогресс загрузки в UI с плавной анимацией.
     */
    private fun updateProgress(bytesRead: Int, totalBytes: Int)
    {
        val progress = (bytesRead * 100) / totalBytes
        runOnUiThreadSafe {
            animateProgress(progress)
            percentView.text = getString(R.string.servers_update_loading_percent, progress)
        }
    }

    /**
     * Анимирует плавное обновление прогресса.
     */
    private fun animateProgress(target: Int)
    {
        val clamped = target.coerceAtMost(100)

        if (progressBar.progress != clamped) {
            ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, clamped).apply {
                duration = 150
                start()
            }
        }
    }

    /**
     * Устанавливает прогресс на 100% с анимацией.
     */
    private fun finishProgress()
    {
        if (!hasStarted) return

        runOnUiThreadSafe {
            animateProgress(100)
            percentView.text = getString(R.string.servers_update_loading_percent, 100)
        }
    }

    /**
     * Проверяет, что фрагмент безопасен для UI-операций:
     * он добавлен в FragmentManager и имеет доступ к Activity и Context.
     */
    private val isUiThreadSafe: Boolean
        get() = isAdded && activity != null && context != null

    /**
     * Безопасно выполняет действие в UI-потоке, если фрагмент всё ещё активен.
     *
     * Предотвращает сбои при доступе к context, activity или вызове getString,
     * если диалог уже был закрыт или уничтожен.
     *
     * @param action Действие, которое нужно выполнить на главном потоке
     */
    private fun runOnUiThreadSafe(action: () -> Unit) {
        if (!isUiThreadSafe) return
        activity?.runOnUiThread {
            if (isUiThreadSafe) action()
        }
    }

    /**
     * Показывает короткое сообщение Toast.
     */
    private fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
