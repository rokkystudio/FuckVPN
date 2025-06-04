package fuck.system.vpn.servers.dialogs

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import fuck.system.vpn.R
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max

class GetServersDialog : DialogFragment()
{
    companion object {
        const val TAG = "GetServersDialog"
        const val URL_KEY = "URL"
        const val RESULT_KEY = "GetServersResult"
        const val RESULT_EXTRA = "CSV"

        fun newInstance(csvUrl: String): GetServersDialog {
            val fragment = GetServersDialog()
            fragment.arguments = Bundle().apply {
                putString(URL_KEY, csvUrl)
            }
            return fragment
        }
    }

    private lateinit var textMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var percentView: TextView
    private lateinit var errorActions: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var closeButton: Button

    @Volatile private var cancelled = false
    private var job: Job? = null
    private var hasStarted = false
    private var csvUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        csvUrl = arguments?.getString(URL_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_get_servers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textMessage = view.findViewById(R.id.GetServersTextMessage)
        progressBar = view.findViewById(R.id.GetServersProgressBar)
        percentView = view.findViewById(R.id.GetServersPercentText)
        errorActions = view.findViewById(R.id.GetServersErrorActions)
        retryButton = view.findViewById(R.id.GetServersRetryButton)
        closeButton = view.findViewById(R.id.GetServersCloseButton)

        retryButton.setOnClickListener {
            errorActions.visibility = View.GONE
            textMessage.setText(R.string.servers_get_loading)
            startLoading()
        }

        closeButton.setOnClickListener {
            cancelled = true
            dismissAllowingStateLoss()
        }

        if (!hasStarted) {
            hasStarted = true
            startLoading()
        }
    }

    private fun startLoading()
    {
        val urlStr = csvUrl ?: return
        progressBar.progress = 0
        percentView.text = ""
        errorActions.visibility = View.GONE
        textMessage.setText(R.string.servers_get_loading)

        job = lifecycleScope.launch(Dispatchers.IO) {
            val start = System.currentTimeMillis()
            val csv = try {
                downloadCsvWithProgress(urlStr)
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Ошибка загрузки: ${e.message ?: "Unknown error"}")
                null
            }

            delay(max(0, 1000 - (System.currentTimeMillis() - start)))

            if (!cancelled && isAdded && csv != null) {
                withContext(Dispatchers.Main) {
                    parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle().apply {
                        putString(RESULT_EXTRA, csv)
                    })
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    private fun showError(message: String) {
        if (!isAdded) return
        lifecycleScope.launch(Dispatchers.Main) {
            textMessage.text = message
            progressBar.progress = 0
            percentView.text = ""
            errorActions.visibility = View.VISIBLE
        }
    }

    private fun downloadCsvWithProgress(urlStr: String): String? {
        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000

        if (connection.responseCode != 200) {
            showError("Ошибка: HTTP ${connection.responseCode}")
            return null
        }

        val length = connection.contentLength
        connection.inputStream.use { inputStream ->
            return readStreamWithProgress(inputStream, length)
        }
    }

    private fun readStreamWithProgress(inputStream: java.io.InputStream, totalLength: Int): String {
        val buffer = ByteArray(4096)
        val out = StringBuilder()
        var totalRead = 0
        var read: Int
        var lastProgress = -1

        while (inputStream.read(buffer).also { read = it } != -1 && !cancelled) {
            out.append(String(buffer, 0, read))
            totalRead += read
            lastProgress = maybeUpdateProgress(totalRead, totalLength, lastProgress)
        }

        showFinalProgress()
        return out.toString()
    }

    private fun maybeUpdateProgress(totalRead: Int, totalLength: Int, lastProgress: Int): Int {
        if (totalLength <= 0 || !isAdded) return lastProgress

        val progress = (totalRead * 100) / totalLength
        if (progress != lastProgress) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (isAdded && context != null) {
                    progressBar.progress = progress
                    percentView.text = getString(R.string.servers_get_loading_percent, progress)
                }
            }
        }
        return progress
    }

    private fun showFinalProgress() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                progressBar.progress = 100
                percentView.text = getString(R.string.servers_get_loading_percent, 100)
            }
        }
    }

    override fun onDestroyView() {
        cancelled = true
        job?.cancel()
        super.onDestroyView()
    }
}
