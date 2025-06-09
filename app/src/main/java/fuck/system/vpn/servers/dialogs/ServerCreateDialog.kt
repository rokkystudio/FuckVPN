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
import fuck.system.vpn.servers.parser.OvpnParser
import fuck.system.vpn.servers.parser.ServersParser
import fuck.system.vpn.servers.server.ServerItem
import fuck.system.vpn.servers.server.ServerStorage

/**
 * Диалог для ручного добавления нового VPN-сервера.
 *
 * Позволяет пользователю ввести или загрузить .ovpn-конфигурацию,
 * автоматически парсит из неё IP, порт, протокол и страну,
 * и сохраняет сервер в локальное хранилище, если он уникален.
 *
 * Защищён от добавления дубликатов по IP-адресу.
 */
class ServerCreateDialog : DialogFragment()
{
    companion object {
        const val TAG = "AddServerDialog"
    }

    /**
     * Регистрирует обработчик результата выбора файла пользователем.
     * Используется для загрузки содержимого .ovpn-файла в текстовое поле.
     */
    private val filePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(), ::onPickerResult
    )

    /**
     * Обрабатывает результат выбора файла.
     * Считывает содержимое выбранного .ovpn-файла и помещает его в соответствующее поле.
     *
     * @param uri URI выбранного пользователем файла или null, если выбор отменён.
     */
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
            e.printStackTrace()
            edit.setText("")
            Toast.makeText(requireContext(), getString(R.string.server_create_load_file_error), Toast.LENGTH_LONG).show()
            edit.requestFocus()
        }
    }

    /**
     * Создаёт диалог для ручного добавления нового сервера.
     * Инициализирует элементы интерфейса и обрабатывает действия кнопок.
     *
     * @return созданный экземпляр диалога
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
     * Валидирует конфигурацию, парсит её, проверяет на дубликаты и сохраняет новый сервер.
     *
     * @param inputName поле ввода имени сервера (необязательно)
     * @param inputOvpn поле ввода конфигурации OpenVPN (обязательно)
     */
    private fun onSaveClicked(inputName: EditText?, inputOvpn: EditText?)
    {
        val name = inputName?.text?.toString()?.trim().orEmpty()
        val ovpn = inputOvpn?.text?.toString()?.trim().orEmpty()

        if (ovpn.isBlank()) {
            inputOvpn?.error = getString(R.string.server_create_openvpn_required)
            return
        }

        val info = OvpnParser.parse(ovpn)
        val ip = ServersParser.getIpFromOvpn(ovpn, null)

        if (ip == null) {
            Toast.makeText(requireContext(), getString(R.string.server_create_invalid_config), Toast.LENGTH_LONG).show()
            return
        }

        // Проверка на дубликат IP
        val servers = ServerStorage.load(requireContext())
        if (servers.any { it.ip == ip }) {
            Toast.makeText(requireContext(), getString(R.string.server_create_duplicate), Toast.LENGTH_LONG).show()
            return
        }

        val item = ServerItem(
            name = if (name.isNotBlank()) name else ip,
            ovpn = ovpn,
            favorite = false,
            ip = ip,
            port = info.port,
            country = info.country,
            proto = info.proto,
            ping = null
        )

        val updated = servers.toMutableList().apply { add(item) }
        ServerStorage.save(requireContext(), updated)

        Toast.makeText(requireContext(), getString(R.string.server_create_complete), Toast.LENGTH_SHORT).show()
        dismiss()
    }
}