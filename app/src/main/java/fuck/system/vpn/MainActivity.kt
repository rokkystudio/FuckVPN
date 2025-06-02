package fuck.system.vpn

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import fuck.system.vpn.databinding.ActivityMainBinding
import fuck.system.vpn.servers.ServersFragment

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Включаем тёмную тему принудительно
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Получаем версию приложения и устанавливаем заголовок
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.appTitle).text = "FUCK VPN v$versionName"

        // Загружаем фрагмент со списком серверов при запуске
        loadFragment(ServersFragment())

        // Обработка выбора пунктов нижней навигации
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_servers -> loadFragment(ServersFragment())
                R.id.nav_status -> loadFragment(StatusFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager

        // Очистка back stack перед загрузкой нового фрагмента
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }

        // Замена фрагмента в контейнере
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}