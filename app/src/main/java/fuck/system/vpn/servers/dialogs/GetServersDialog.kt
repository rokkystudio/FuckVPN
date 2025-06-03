package fuck.system.vpn.servers.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class GetServersDialog : DialogFragment()
{
    private var onResult: ((String?) -> Unit)? = null
    private var csvUrl: String? = null

    @Volatile private var cancelled = false

    companion object {
        const val TAG = "GetServersDialog"

        fun newInstance(csvUrl: String, onResult: (String?) -> Unit): GetServersDialog {
            val fragment = GetServersDialog()
            fragment.onResult = onResult
            fragment.csvUrl = csvUrl
            return fragment
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val view = layoutInflater.inflate(R.layout.dialog_get_servers, null, false)
        val textMessage = view.findViewById<TextView>(R.id.GetServersTextMessage)
        textMessage.text = getString(R.string.servers_get_loading)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.servers_get_title))
            .setView(view)
            .setCancelable(false)
            .setNegativeButton(R.string.cancel) { _, _ ->
                cancelled = true
                dismiss()
            }
            .create()

        // Никаких setOnShowListener тут не надо!
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? AlertDialog ?: return
        val textMessage = dialog.findViewById<TextView>(R.id.GetServersTextMessage) ?: return
        startLoading(textMessage)
    }

    private fun startLoading(textView: TextView) {
        val urlStr = csvUrl ?: return
        thread {
            val csv = downloadCsvWithProgress(textView, urlStr)
            if (!cancelled && isAdded) {
                requireActivity().runOnUiThread {
                    onResult?.invoke(csv)
                    dismiss()
                }
            }
        }
    }

    private fun downloadCsvWithProgress(statusView: TextView, urlStr: String): String? {
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode != 200) return null

            val length = connection.contentLength
            connection.inputStream.use { inputStream ->
                return readStreamWithProgress(inputStream, length, statusView)
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun readStreamWithProgress(
        inputStream: java.io.InputStream,
        totalLength: Int,
        statusView: TextView
    ): String {
        val buffer = ByteArray(4096)
        val out = StringBuilder()
        var totalRead = 0
        var read: Int
        var lastProgress = -1

        while (inputStream.read(buffer).also { read = it } != -1 && !cancelled) {
            out.append(String(buffer, 0, read))
            totalRead += read
            if (totalLength > 0) {
                val progress = (totalRead * 100) / totalLength
                if (progress != lastProgress && isAdded) {
                    lastProgress = progress
                    requireActivity().runOnUiThread {
                        statusView.text = getString(R.string.servers_get_loading_percent, progress)
                    }
                }
            }
        }
        return out.toString()
    }
}
