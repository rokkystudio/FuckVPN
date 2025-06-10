package fuck.system.vpn.servers.dialogs

import android.app.Dialog
import android.os.Bundle
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = Dialog(requireContext(), theme)
        dialog.setContentView(R.layout.dialog_servers_clear)
        dialog.setTitle(getString(R.string.servers_clear_title))

        val buttonClear = dialog.findViewById<Button>(R.id.ServersEmptyButtonClear)
        val buttonCancel = dialog.findViewById<Button>(R.id.ServersEmptyButtonCancel)

        buttonClear?.setOnClickListener {
            // Очищаем список серверов
            ServersStorage.save(requireContext(), emptyList())
            Toast.makeText(requireContext(), R.string.servers_clear_complete, Toast.LENGTH_SHORT).show()
            dismiss()
        }

        buttonCancel?.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}
