package fuck.system.vpn.ping

import android.content.Intent
import android.net.VpnService
import android.os.*
import fuck.system.vpn.servers.server.ServersStorage
import kotlinx.coroutines.*
import java.io.FileDescriptor
import java.io.FileInputStream
import kotlin.coroutines.coroutineContext

class PingService : VpnService() {
    private var tunInterface: ParcelFileDescriptor? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var clientMessenger: Messenger? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = applicationContext

        // Загружаем список серверов из ServersStorage
        val servers = ServersStorage.load(context)
            .filter { it.ip != null && it.port != null && it.proto != null }
            .toMutableList()

        if (servers.isEmpty()) return START_NOT_STICKY

        // Извлекаем Messenger для обратной связи с PingServersDialog
        clientMessenger = extractMessenger(intent)

        setupTunInterface()

        tunInterface?.fileDescriptor?.let { fd ->
            val sentTimes = mutableMapOf<String, Long>()
            val rtts = mutableMapOf<String, Int>()

            PacketParser.sentTimes = sentTimes
            PacketParser.clientMessenger = clientMessenger
            PacketParser.rtts = rtts

            // Запускаем приём входящих пакетов
            coroutineScope.launch { processIncomingPackets(fd) }

            // Отправляем пинги и завершаем работу
            coroutineScope.launch {
                val udp = servers.filter { it.proto.equals("udp", true) }
                    .map { it.ip!! to it.port!! }

                val tcp = servers.filter { it.proto.equals("tcp", true) }
                    .map { it.ip!! to it.port!! }

                if (udp.isNotEmpty()) {
                    PacketSender.sendUdpToServers(fd, udp, sentTimes)
                }

                if (tcp.isNotEmpty()) {
                    PacketSender.sendTcpToServers(fd, tcp, sentTimes)
                }

                // Ожидаем приёма ответов
                delay(1100)

                // Применяем результаты RTT к списку серверов
                var responded = 0
                servers.forEach { server ->
                    val ip = server.ip ?: return@forEach
                    val rtt = rtts[ip]
                    if (rtt != null) {
                        server.ping = rtt
                        responded++
                    } else {
                        server.ping = null
                    }
                }

                // Сохраняем обновлённые значения в хранилище
                ServersStorage.save(context, servers)

                // Отправляем статистику в диалог
                clientMessenger?.send(
                    Message.obtain(null, 1, responded, servers.size)
                )

                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    /**
     * Создаёт и настраивает TUN-интерфейс для захвата ICMP через VpnService
     */
    private fun setupTunInterface() {
        val builder = Builder()
        builder.setSession("ServerPing")
            .addAddress("10.0.0.2", 32)
            .addDnsServer("1.1.1.1")
            .addRoute("0.0.0.0", 0)

        tunInterface = builder.establish()
    }

    /**
     * Запускает цикл чтения и анализа входящих пакетов
     */
    private suspend fun processIncomingPackets(fd: FileDescriptor) {
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

    /**
     * Извлекает Messenger для связи с диалогом, если был передан
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
     * Очистка TUN-интерфейса и остановка корутин при уничтожении сервиса
     */
    override fun onDestroy() {
        tunInterface?.close()
        coroutineScope.cancel()
        super.onDestroy()
    }
}
