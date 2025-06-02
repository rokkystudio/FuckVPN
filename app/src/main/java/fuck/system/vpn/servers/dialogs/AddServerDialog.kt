package fuck.system.vpn.servers.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem

class AddServerDialog : DialogFragment() {

    private var onResult: ((ServerItem) -> Unit)? = null

    // Поля диалога
    private lateinit var inputName: EditText
    private lateinit var inputIp: EditText
    private lateinit var inputPort: EditText
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var vpnTypeSpinner: Spinner
    private lateinit var inputOvpnKey: EditText
    private lateinit var inputPsk: EditText
    private lateinit var buttonCreate: Button
    private lateinit var buttonCancel: Button

    companion object {
        const val TAG = "AddServerDialog"
        fun newInstance(onResult: (ServerItem) -> Unit): AddServerDialog {
            val fragment = AddServerDialog()
            fragment.onResult = onResult
            return fragment
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val view = layoutInflater.inflate(R.layout.dialog_add_server, null, false)
        val dialog = Dialog(requireContext())
        dialog.setContentView(view)
        dialog.setTitle("Добавить сервер вручную")

        // Инициализация виджетов (строго по id из layout)
        inputName = view.findViewById(R.id.ServerAddName)
        inputIp = view.findViewById(R.id.inputIp)
        inputPort = view.findViewById(R.id.inputPort)
        inputOvpnKey = view.findViewById(R.id.ServerAddOpenVpn)
        inputPsk = view.findViewById(R.id.inputPsk)
        buttonCreate = view.findViewById(R.id.ServerAddSave)
        buttonCancel = view.findViewById(R.id.ServersAddCancel)

        // Значения по умолчанию
        inputPort.setText(defaultPort.toString())
        inputUsername.setText(defaultLogin)
        inputPassword.setText(defaultPassword)

        // Spinner всегда выбирает первый элемент (OpenVPN)
        updateFieldsVisibility(vpnTypeSpinner.selectedItemPosition)

        vpnTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateFieldsVisibility(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonCreate.setOnClickListener {
            onCreateClicked()
        }
        buttonCancel.setOnClickListener {
            dismiss()
        }
        return dialog
    }

    private fun updateFieldsVisibility(selected: Int) {
        // 0: OpenVPN, 1: L2TP/IPSec, 2: PPTP (см. vpntype_array)
        when (selected) {
            0 -> { // OpenVPN
                inputOvpnKey.visibility = android.view.View.VISIBLE
                inputPsk.visibility = android.view.View.GONE
            }
            1 -> { // L2TP/IPSec
                inputOvpnKey.visibility = android.view.View.GONE
                inputPsk.visibility = android.view.View.VISIBLE
            }
            else -> { // PPTP или другие
                inputOvpnKey.visibility = android.view.View.GONE
                inputPsk.visibility = android.view.View.GONE
            }
        }
    }

    private fun onCreateClicked() {
        val validatedData = validateInput() ?: return
        createServerAndReturnResult(validatedData)
    }

    private data class ServerInputData(
        val name: String?,
        val vpntype: String,
        val ip: String,
        val port: Int,
        val username: String,
        val password: String,
        val psk: String?,
        val ovpn: String?
    )

    private fun validateInput(): ServerInputData? {
        val name = inputName.text.toString().trim().ifEmpty { null }
        val ip = inputIp.text.toString().trim()
        val portStr = inputPort.text.toString().trim()

        if (ip.isEmpty() || portStr.isEmpty()) {
            Toast.makeText(requireContext(), "IP адрес и Port обязательны!", Toast.LENGTH_SHORT).show()
            return null
        }

        val port = portStr.toIntOrNull()
        if (port == null || port !in 1..65535) {
            Toast.makeText(requireContext(), "Порт должен быть числом от 1 до 65535", Toast.LENGTH_SHORT).show()
            return null
        }

        val selectedType = vpnTypeSpinner.selectedItemPosition
        val ovpn = if (selectedType == 0) inputOvpnKey.text.toString().trim().ifEmpty { null } else null

        return ServerInputData(name, ip, port, ovpn)
    }

    private fun createServerAndReturnResult(data: ServerInputData) {
        val server = ServerItem(
            name = data.name,
            ip = data.ip,
            port = data.port,
            country = null, // автоопределение через geolite.mmdb по IP в другом месте
            ping = null,
            favorite = true,
            ovpn = data.ovpn
        )
        onResult?.invoke(server)
        dismiss()
    }
}
