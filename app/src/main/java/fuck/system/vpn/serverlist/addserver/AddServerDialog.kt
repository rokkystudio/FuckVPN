package fuck.system.vpn.serverlist.addserver

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.serverlist.ServerListItem

class AddServerDialog : DialogFragment()
{
    private var onResult: ((ServerListItem) -> Unit)? = null

    // Поля диалога
    private lateinit var inputCountry: EditText
    private lateinit var inputIp: EditText
    private lateinit var inputPort: EditText
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputProtocol: Spinner
    private lateinit var buttonCreate: Button
    private lateinit var buttonCancel: Button

    // Значения по умолчанию
    private val defaultPort = resources.getInteger(R.integer.default_vpn_port)
    private val defaultLogin = getString(R.string.default_login)
    private val defaultPassword = getString(R.string.default_password)
    private val defaultProtocol = getString(R.string.default_protocol)

    companion object {
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
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_server, null, false)

        dialog.setContentView(view)
        dialog.setTitle("Добавить сервер вручную")

        // Инициализация виджетов
        inputCountry = view.findViewById(R.id.inputCountry)
        inputIp = view.findViewById(R.id.inputIp)
        inputPort = view.findViewById(R.id.inputPort)
        inputUsername = view.findViewById(R.id.inputUsername)
        inputPassword = view.findViewById(R.id.inputPassword)
        inputProtocol = view.findViewById(R.id.protocolSpinner)
        buttonCreate = view.findViewById(R.id.buttonCreate)
        buttonCancel = view.findViewById(R.id.buttonCancel)

        // Применение дефолтных значений к полям ввода
        inputPort.setText(defaultPort.toString())
        inputUsername.setText(defaultLogin)
        inputPassword.setText(defaultPassword)
        // spinner можно настроить при необходимости, например, выбрать defaultProtocol, если он есть в списке

        buttonCreate.setOnClickListener {
            onCreateClicked()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    private fun onCreateClicked() {
        val validatedData = validateInput() ?: return
        createServerAndReturnResult(validatedData)
    }

    private data class ServerInputData(
        val country: String,
        val ip: String,
        val port: Int,
        val username: String,
        val password: String,
        val protocol: String
    )

    private fun validateInput(): ServerInputData?
    {
        val country = inputCountry.text.toString().trim()
        val ip = inputIp.text.toString().trim()
        val portStr = inputPort.text.toString().trim()
        val username = inputUsername.text.toString().trim().ifEmpty { defaultLogin }
        val password = inputPassword.text.toString().trim().ifEmpty { defaultPassword }
        val protocol = inputProtocol.selectedItem as? String ?: defaultProtocol

        if (ip.isEmpty() || portStr.isEmpty()) {
            Toast.makeText(requireContext(), "IP адрес и Port обязательны!", Toast.LENGTH_SHORT).show()
            return null
        }

        val port = portStr.toIntOrNull()
        if (port == null || port !in 1..65535) {
            Toast.makeText(requireContext(), "Порт должен быть числом от 1 до 65535", Toast.LENGTH_SHORT).show()
            return null
        }

        return ServerInputData(country, ip, port, username, password, protocol)
    }


    private fun createServerAndReturnResult(data: ServerInputData)
    {
        val server = ServerListItem(
            name = null,
            ip = data.ip,
            port = data.port,
            country = data.country,
            ping = null,
            favorite = true,
            username = data.username,
            password = data.password,
            protocol = data.protocol,
            psk = null,
            ovpn = null
        )
        onResult?.invoke(server)
        dismiss()
    }
}
