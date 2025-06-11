package fuck.system.vpn.servers.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServersStorage

/**
 * Диалог подтверждения очистки списка серверов.
 * Очищает хранилище серверов при подтверждении, без возврата результата.
 */
class ServersClearDialog : DialogFragment()
{
    companion object {
        const val TAG = "ServersClearDialog"
    }

    override fun getTheme(): Int = R.style.DialogTheme

    /**
     * Загружает layout диалога.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_servers_clear, container, false)
    }

    /**
     * Инициализирует кнопки и их действия.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.ServersEmptyButtonClear).setOnClickListener {
            clearServers()
        }

        view.findViewById<Button>(R.id.ServersEmptyButtonCancel).setOnClickListener {
            dismiss()
        }
    }

    /**
     * Удаляет все серверы из хранилища и закрывает диалог.
     */
    private fun clearServers() {
        ServersStorage.save(requireContext(), emptyList())
        Toast.makeText(requireContext(), R.string.servers_clear_complete, Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
