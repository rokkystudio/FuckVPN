package fuck.system.vpn.serverlist.addserver

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.serverlist.ServerListItem

class AddServerDialog : DialogFragment()
{
    private var onResult: ((ServerListItem) -> Unit)? = null

    companion object
    {
        const val TAG = "AddServerDialog"

        fun newInstance(onResult: (ServerListItem) -> Unit): AddServerDialog {
            val fragment = AddServerDialog()
            fragment.onResult = onResult
            return fragment
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val view = layoutInflater.inflate(R.layout.dialog_add_server, null, false)
        val inputCountry = view.findViewById<EditText>(R.id.inputCountry)
        val inputIp = view.findViewById<EditText>(R.id.inputIp)
        val inputPort = view.findViewById<EditText>(R.id.inputPort)
        val inputUsername = view.findViewById<EditText>(R.id.inputUsername)
        val inputPassword = view.findViewById<EditText>(R.id.inputPassword)
        val inputProtocol = view.findViewById<Spinner>(R.id.protocolSpinner)

        val defaultPort = resources.getInteger(R.integer.default_vpn_port)
        val defaultLogin = getString(R.string.default_login)
        val defaultPassword = getString(R.string.default_password)
        val defaultProtocol = requireContext().getString(R.string.protocol_udp)

        return AlertDialog.Builder(requireContext())
            .setTitle("Добавить сервер вручную")
            .setView(view)
            .setPositiveButton("Добавить") { _, _ ->
                val ip = inputIp.text.toString().trim()
                val portStr = inputPort.text.toString().trim()
                val username = inputUsername.text.toString().trim().ifEmpty { defaultLogin }
                val password = inputPassword.text.toString().trim().ifEmpty { defaultPassword }
                val protocol = (inputProtocol.selectedItem as? String) ?: defaultProtocol

                if (ip.isEmpty() || portStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Поля IP, порт обязательны", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val port = portStr.toIntOrNull()
                if (port == null || port !in 1..65535) {
                    Toast.makeText(requireContext(), "Порт должен быть числом от 1 до 65535", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val server = ServerListItem(
                    ip = ip,
                    country = country,
                    port = port,
                    ping = 999,
                    favorite = true,
                    username = username,
                    password = password,
                    protocol = protocol,
                    openVpnConfigBase64 = null
                )
                onResult?.invoke(server)
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
}
