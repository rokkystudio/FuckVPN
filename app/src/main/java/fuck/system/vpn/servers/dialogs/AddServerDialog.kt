package fuck.system.vpn.servers.dialogs

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem

class AddServerDialog : DialogFragment()
{
    private var onResult: ((ServerItem) -> Unit)? = null

    companion object {
        const val TAG = "AddServerDialog"
        fun newInstance(onResult: (ServerItem) -> Unit): AddServerDialog {
            val fragment = AddServerDialog()
            fragment.onResult = onResult
            return fragment
        }
    }

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(), ::onPickerResult
    )

    private fun onPickerResult(uri: Uri?)
    {
        if (uri == null) return

        val edit = dialog?.findViewById<EditText>(R.id.ServerAddOpenVpn) ?: return

        try {
            val resolver = requireContext().contentResolver
            resolver.openInputStream(uri)?.use { stream ->
                val text = stream.bufferedReader().readText()
                edit.setText(text)
            }
        } catch (e: Exception) {
            edit.error = getString(R.string.server_add_load_file_error)
        }
    }

    // Новый обработчик кнопки
    private fun onSaveClicked(inputName: EditText?, inputOvpn: EditText?)
    {
        val name = inputName?.text?.toString()?.trim().orEmpty()
        val ovpn = inputOvpn?.text?.toString()?.trim().orEmpty()

        if (ovpn.isBlank()) {
            inputOvpn?.error = getString(R.string.server_add_openvpn_required)
            return
        }

        val item = ServerItem(
            name = name, ovpn = ovpn, favorite = false,
            ip = null, port = null, country = null, ping = null
        )
        onResult?.invoke(item)
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_server)
        dialog.setTitle(getString(R.string.servers_add_server))

        val inputName = dialog.findViewById<EditText>(R.id.ServerAddName)
        val inputOvpn = dialog.findViewById<EditText>(R.id.ServerAddOpenVpn)
        val buttonSave = dialog.findViewById<Button>(R.id.ServerAddSave)
        val buttonCancel = dialog.findViewById<Button>(R.id.ServersAddCancel)
        val buttonLoadFile = dialog.findViewById<Button>(R.id.ServerAddLoadFile)

        buttonSave?.setOnClickListener { onSaveClicked(inputName, inputOvpn) }

        buttonCancel?.setOnClickListener { dismiss() }

        buttonLoadFile?.setOnClickListener {
            filePicker.launch(arrayOf(
                "*/*",
                "application/x-openvpn-profile",
                "application/octet-stream",
                "text/*"
            ))
        }

        return dialog
    }
}
