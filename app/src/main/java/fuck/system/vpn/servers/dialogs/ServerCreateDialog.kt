package fuck.system.vpn.servers.dialogs

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.parser.ServersParser
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServersStorage

/**
 * Диалог для ручного добавления нового VPN-сервера.
 *
 * Позволяет пользователю ввести или загрузить .ovpn-конфигурацию,
 * парсит из неё параметры (host, ip, port, proto, country)
 * и сохраняет сервер в локальное хранилище.
 *
 * Если сервер с таким IP уже существует — он будет обновлён.
 */
class ServerCreateDialog : DialogFragment()
{
    companion object {
        const val TAG = "AddServerDialog"
    }

    override fun getTheme(): Int = R.style.DialogTheme

    /**
     * Регистрирует обработчик выбора файла .ovpn из хранилища.
     * Загружает содержимое в текстовое поле.
     */
    private val filePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(), ::onPickerResult
    )

    /**
     * Обрабатывает результат выбора .ovpn-файла.
     * Считывает содержимое и вставляет в текстовое поле.
     */
    private fun onPickerResult(uri: Uri?)
    {
        if (uri == null || !isAdded || dialog == null || view == null) return

        val edit = dialog?.findViewById<EditText>(R.id.ServerAddOpenVpn) ?: return

        try {
            val resolver = requireContext().contentResolver
            resolver.openInputStream(uri)?.use { stream ->
                val text = stream.bufferedReader().readText()
                edit.setText(text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            edit.setText("")
            Toast.makeText(requireContext(), getString(R.string.server_create_load_file_error), Toast.LENGTH_LONG).show()
            edit.requestFocus()
        }
    }

    /**
     * Создаёт диалоговое окно с полями для ввода имени и вставки конфигурации.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_server_create)
        dialog.setTitle(getString(R.string.servers_add_server))

        val inputName = dialog.findViewById<EditText>(R.id.ServerAddName)
        val inputOvpn = dialog.findViewById<EditText>(R.id.ServerAddOpenVpn)
        val buttonSave = dialog.findViewById<Button>(R.id.ServerAddSave)
        val buttonCancel = dialog.findViewById<Button>(R.id.ServersAddCancel)
        val buttonLoadFile = dialog.findViewById<Button>(R.id.ServerAddLoadFile)

        buttonSave?.setOnClickListener { onSaveClicked(inputName, inputOvpn) }
        buttonCancel?.setOnClickListener { dismiss() }
        buttonLoadFile?.setOnClickListener {
            filePicker.launch(
                arrayOf(
                    "*/*",
                    "application/x-openvpn-profile",
                    "application/octet-stream",
                    "text/*"
                )
            )
        }

        return dialog
    }

    /**
     * Обрабатывает нажатие кнопки "Сохранить".
     * Парсит конфигурацию, извлекает ключевые данные и сохраняет сервер.
     * Если сервер с таким IP уже есть — заменяет.
     */
    private fun onSaveClicked(inputName: EditText?, inputOvpn: EditText?)
    {
        val ovpn = inputOvpn?.text?.toString()?.trim().orEmpty()

        if (ovpn.isBlank()) {
            inputOvpn?.error = getString(R.string.server_create_openvpn_required)
            return
        }

        val host = ServersParser.getRemoteHost(ovpn)
        val ip = ServersParser.resolveIp(host, null)

        if (ip == null) {
            Toast.makeText(requireContext(), getString(R.string.server_create_invalid_config), Toast.LENGTH_LONG).show()
            return
        }

        val port = ServersParser.getPortFromOvpn(ovpn)
        val proto = ServersParser.getProtoFromOvpn(ovpn)
        val country = ServersParser.getCountryFromOvpn(ovpn)

        var name = inputName?.text?.toString()?.trim().orEmpty()
        name = name.ifBlank { host ?: ip }

        val item = ServerItem(
            name = name,
            ovpn = ovpn,
            favorite = false,
            ip = ip,
            port = port,
            proto = proto,
            country = country,
            ping = null
        )

        val context = requireContext()
        val servers = ServersStorage.load(context)
        val existingIndex = servers.indexOfFirst { it.ip == ip }

        val isUpdate = existingIndex >= 0
        if (isUpdate) {
            servers[existingIndex] = item
        } else {
            servers.add(item)
        }

        ServersStorage.save(context, servers)

        val msg = if (isUpdate) {
            getString(R.string.server_create_updated)
        } else {
            getString(R.string.server_create_complete)
        }

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
