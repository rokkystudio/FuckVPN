package fuck.system.vpn.ping

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import java.io.FileInputStream

class PingService : VpnService()
{
    private var tunInterface: ParcelFileDescriptor? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serverList = intent?.getStringArrayListExtra("servers") ?: return START_NOT_STICKY

        val builder = Builder()
        builder.setSession("ServerPing")
            .addAddress("10.0.0.2", 32)
            .addDnsServer("1.1.1.1")
            .addRoute("0.0.0.0", 0)

        tunInterface = builder.establish()

        tunInterface?.fileDescriptor?.let { fd ->
            coroutineScope.launch {
                processIncomingPackets(fd)
            }

            coroutineScope.launch {
                PacketSender.sendUdpToServers(fd, serverList)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun processIncomingPackets(fd: FileDescriptor)
    {
        val input = FileInputStream(fd)
        val buffer = ByteArray(32767)

        while (coroutineContext.isActive) {
            val length = input.read(buffer)
            if (length > 0) {
                val packet = buffer.copyOf(length)
                PacketParser.parse(packet)
            }
        }
    }

    override fun onDestroy() {
        tunInterface?.close()
        coroutineScope.cancel()
        super.onDestroy()
    }
}
