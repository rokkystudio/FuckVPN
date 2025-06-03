package fuck.system.vpn.status

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import com.marsounjan.icmp4a.Icmp4a
import com.marsounjan.icmp4a.Icmp

class PingObserver(private val host: String, private val intervalMs: Long = 5000)
{
    private val icmp = Icmp4a()
    private var job: Job? = null
    val liveData = MutableLiveData<String>()

    fun start(scope: CoroutineScope) {
        stop()
        job = scope.launch(Dispatchers.IO) {
            pingLoop()
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun observe(owner: LifecycleOwner, onResult: (String) -> Unit) {
        liveData.observe(owner, Observer { onResult(it) })
    }

    private suspend fun CoroutineScope.pingLoop()
    {
        while (isActive)
        {
            val resultText = try {
                val status = icmp.ping(host)
                when (val result = status.result) {
                    is Icmp.PingResult.Success -> "Ping: ${result.ms} ms"
                    is Icmp.PingResult.Failed -> "Ping failed: ${result.message}"
                }
            } catch (e: Exception) {
                "Ping error: ${e.message}"
            }
            liveData.postValue(resultText)
            delay(intervalMs)
        }
    }
}
