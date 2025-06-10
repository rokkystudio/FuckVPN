package fuck.system.vpn.servers.ping

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import fuck.system.vpn.servers.server.ServersStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.net.*

class PingService : Service()
{
    companion object {
        /** Максимальное количество одновременно выполняемых пингов */
        private const val MAX_CONCURRENT_PINGS = 20
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var clientMessenger: Messenger? = null

    /**
     * Запуск сервиса. Загружает список серверов и запускает параллельный пинг.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PingService", "Service started")

        val context = applicationContext

        val servers = ServersStorage.load(context)
            .filter { it.ip != null && it.port != null && it.proto != null }
            .toMutableList()

        Log.d("PingService", "Loaded ${servers.size} servers from storage")

        if (servers.isEmpty()) return START_NOT_STICKY

        clientMessenger = extractMessenger(intent)

        coroutineScope.launch {
            Log.d("PingService", "Starting parallel ping coroutine")

            val semaphore = Semaphore(MAX_CONCURRENT_PINGS)
            val results = servers.map { server ->
                async {
                    val ip = server.ip ?: return@async null
                    val port = server.port ?: return@async null
                    val proto = server.proto?.lowercase() ?: return@async null

                    semaphore.withPermit {
                        val rtt = when (proto) {
                            "udp" -> tryUdpHandshake(ip, port)
                            "tcp" -> tryTcpConnect(ip, port)
                            else -> null
                        }

                        if (rtt != null) {
                            server.ping = rtt
                            sendProgressUpdate(ip, rtt)
                            server
                        } else {
                            server.ping = null
                            sendProgressUpdate(ip, null)
                            null
                        }
                    }
                }
            }

            val responded = results.mapNotNull { it.await() }.count()

            Log.d("PingService", "Ping complete: $responded of ${servers.size} responded")

            ServersStorage.save(context, servers)

            clientMessenger?.send(
                Message.obtain(null, 1, responded, servers.size)
            )

            stopSelf()
        }

        return START_NOT_STICKY
    }

    /**
     * Пингует сервер по TCP. Возвращает RTT или null при ошибке.
     */
    private fun tryTcpConnect(ip: String, port: Int): Int? {
        return try {
            val socket = Socket()
            val start = System.currentTimeMillis()
            socket.connect(InetSocketAddress(ip, port), 1000)
            val rtt = (System.currentTimeMillis() - start).toInt()
            socket.close()
            Log.d("PingService", "TCP RTT to $ip:$port = $rtt ms")
            rtt
        } catch (e: Exception) {
            Log.w("PingService", "TCP failed for $ip:$port: ${e.message}")
            null
        }
    }

    /**
     * Отправляет OpenVPN-подобный handshake по UDP и измеряет RTT.
     */
    private fun tryUdpHandshake(ip: String, port: Int): Int? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 1000

            val fakeHello = byteArrayOf(
                0x38, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x0a,
                *"HelloVPN".toByteArray()
            )

            val packet = DatagramPacket(fakeHello, fakeHello.size, InetAddress.getByName(ip), port)
            val start = System.currentTimeMillis()
            socket.send(packet)

            val buf = ByteArray(512)
            val response = DatagramPacket(buf, buf.size)
            socket.receive(response)
            val rtt = (System.currentTimeMillis() - start).toInt()
            Log.d("PingService", "UDP RTT to $ip:$port = $rtt ms")
            rtt
        } catch (e: Exception) {
            Log.w("PingService", "UDP failed for $ip:$port: ${e.message}")
            null
        }
    }

    /**
     * Отправляет промежуточное сообщение с IP и RTT для обновления UI.
     * RTT = -1 означает, что сервер не ответил.
     */
    private fun sendProgressUpdate(ip: String, rtt: Int?) {
        val msg = Message.obtain().apply {
            data = Bundle().apply {
                putString("ip", ip)
                if (rtt != null)
                    putInt("ping", rtt)
                else
                    putBoolean("no_response", true)
            }
        }
        try {
            clientMessenger?.send(msg)
        } catch (e: Exception) {
            Log.e("PingService", "Failed to send progress for $ip: ${e.message}")
        }
    }

    /**
     * Извлекает Messenger для связи с UI.
     */
    private fun extractMessenger(intent: Intent?): Messenger? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("messenger", Messenger::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra("messenger")
        }
    }

    /**
     * Очистка ресурсов.
     */
    override fun onDestroy() {
        Log.d("PingService", "Service destroyed")
        coroutineScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
