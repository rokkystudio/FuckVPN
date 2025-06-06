package fuck.system.vpn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fuck.system.vpn.databinding.ActivityMainBinding
import fuck.system.vpn.servers.dialogs.GetServersDialogAlert

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    private val delayedDialogHandler = Handler(Looper.getMainLooper())
    private var dialogRunnable: Runnable? = null
    private var isDialogScheduled = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Включаем тёмную тему принудительно
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Получаем версию приложения и устанавливаем заголовок
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.appTitle).text = getString(R.string.app_title, versionName)

        // Ищем NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Связываем BottomNavigation с Navigation
        binding.bottomNavigation.setupWithNavController(navController)

        scheduleDialogLaunch()
    }

    private fun scheduleDialogLaunch() {
        if (isDialogScheduled) return

        dialogRunnable = Runnable {
            if (!isFinishing && !isDestroyed) {
                GetServersDialogAlert(this, this).show()
            }
        }

        delayedDialogHandler.postDelayed(dialogRunnable!!, 1000)
        isDialogScheduled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelDialogLaunch()
    }

    private fun cancelDialogLaunch() {
        dialogRunnable?.let {
            delayedDialogHandler.removeCallbacks(it)
            isDialogScheduled = false
        }
    }
}