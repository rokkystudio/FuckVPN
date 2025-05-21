package fuck.system.vpn.serverlist.addserver

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.serverlist.ServerListItem

class AddServerDialog : DialogFragment()
{
    private var onResult: ((ServerListItem) -> Unit)? = null

    companion object {
        const val TAG = "AddServerDialog"

        fun newInstance(
            onResult: (ServerListItem) -> Unit
        ): AddServerDialog {
            val fragment = AddServerDialog()
            fragment.onResult = onResult
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_server, null)
        val inputCountry = view.findViewById<EditText>(R.id.inputCountry)
        val inputIp = view.findViewById<EditText>(R.id.inputIp)

        return AlertDialog.Builder(requireContext())
            .setTitle("Добавить сервер вручную")
            .setView(view)
            .setPositiveButton("Добавить") { _, _ ->
                val country = inputCountry.text.toString().trim()
                val ip = inputIp.text.toString().trim()

                if (country.isNotEmpty() && ip.isNotEmpty()) {
                    val server = ServerListItem(
                        ip = ip,
                        country = country.lowercase(),
                        ping = 999,
                        favorite = true,
                        openVpnConfigBase64 = "" // Пусто, т.к. неизвестно
                    )
                    onResult?.invoke(server)
                } else {
                    Toast.makeText(requireContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
}
