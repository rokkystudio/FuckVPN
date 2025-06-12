package fuck.system.vpn

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fuck.system.vpn.databinding.ActivityMainBinding

/**
 * Главная активность приложения.
 * Отвечает за инициализацию UI, навигационного графа и проверку разрешений (например, BLUETOOTH).
 */
class MainActivity : AppCompatActivity()
{
    /** Привязка к layout-файлу activity_main.xml через ViewBinding */
    private lateinit var binding: ActivityMainBinding

    /**
     * Лаунчер для запроса разрешения BLUETOOTH_CONNECT (требуется начиная с Android 12 / API 31).
     * Используется через Activity Result API.
     */
    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        // Можно показать тост или лог, но не обязательно
    }

    /**
     * Запрашивает разрешение BLUETOOTH_CONNECT во время выполнения,
     * если это необходимо согласно текущей версии Android.
     */
    private fun requestBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = Manifest.permission.BLUETOOTH_CONNECT
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                bluetoothPermissionLauncher.launch(permission)
            }
        }
    }

    /**
     * Основной метод жизненного цикла активности.
     * Выполняет инициализацию интерфейса, навигации и проверку разрешений.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем версию приложения и устанавливаем заголовок
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.app_title).text = getString(R.string.app_title, versionName)

        // Ищем NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Программно устанавливаем стартовый экран
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph)

        val isVpnConnected = false  // TODO

        if (isVpnConnected) {
            navGraph.setStartDestination(R.id.nav_status)
        } else {
            navGraph.setStartDestination(R.id.nav_servers)
        }

        // Применяем граф в NavController
        navController.graph = navGraph

        // Привязываем после установки графа
        binding.bottomNavigation.setupWithNavController(navController)

        requestBluetooth()
    }
}