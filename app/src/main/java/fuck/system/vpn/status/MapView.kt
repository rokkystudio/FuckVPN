package fuck.system.vpn.status

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL

@SuppressLint("SetJavaScriptEnabled")
class MapView(context: Context, attrs: AttributeSet?) : WebView(context, attrs) {

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Android WebView) AppleWebKit/537.36 (KHTML, like Gecko) Chrome Mobile Safari/537.36"
    }

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            userAgentString = USER_AGENT
        }
    }

    fun update() {
        loadIpInfoBlock()
    }

    fun clear() {
        loadUrl("about:blank")
    }

    private fun loadIpInfoBlock() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val html = URL("https://ipinfo.io/what-is-my-ip").readText()
                val doc = Jsoup.parse(html)

                // Шаг 1: найти <div id="block-geolocation">
                val block = doc.getElementById("block-geolocation")

                // Шаг 2: найти первый <div> внутри него
                val firstDiv = block?.children()?.firstOrNull { it.tagName() == "div" }

                // Шаг 3: найти первый <div> внутри первого div
                val target = firstDiv?.children()?.firstOrNull { it.tagName() == "div" }

                if (target != null) {
                    val extractedHtml = """
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1">
                        <style>
                            body { font-family: sans-serif; padding: 16px; margin: 0; background: white; }
                            table { font-size: 14px; width: 100%; }
                            iframe { width: 100%; height: 200px; border: none; margin-top: 12px; }
                        </style>
                    </head>
                    <body>${target.outerHtml()}</body>
                    </html>
                """.trimIndent()

                    withContext(Dispatchers.Main) {
                        loadDataWithBaseURL(null, extractedHtml, "text/html", "utf-8", null)
                    }
                } else {
                    Log.e("MapView", "Target nested <div> not found")
                }
            } catch (e: Exception) {
                Log.e("MapView", "Failed to load geolocation block", e)
            }
        }
    }

}
