package fuck.system.vpn.servers.dialogs

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
     * Загружает layout диалога.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_server_create, container, false)
    }

    /**
     * Вызывается после создания view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val inputName = view.findViewById<EditText>(R.id.ServerAddName)
        val inputOvpn = view.findViewById<EditText>(R.id.ServerAddOpenVpn)
        val buttonSave = view.findViewById<Button>(R.id.ServerAddSave)
        val buttonCancel = view.findViewById<Button>(R.id.ServersAddCancel)
        val buttonLoadFile = view.findViewById<Button>(R.id.ServerAddLoadFile)

        buttonLoadFile?.setOnClickListener {
            filePicker.launch(arrayOf(
                "*/*", "application/x-openvpn-profile", "application/octet-stream", "text/*"
            ))
        }

        buttonSave?.setOnClickListener {
            onSaveClicked(inputName, inputOvpn)
        }

        buttonCancel?.setOnClickListener {
            dismiss()
        }
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

        val server = ServerItem(
            name = name,
            ovpn = ovpn,
            favorite = false,
            ip = ip,
            port = port,
            proto = proto,
            country = country,
            ping = null
        )

        saveServer(server)

        Toast.makeText(context, R.string.server_create_complete, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    /**
     * Сохраняет сервер в локальное хранилище.
     * Если сервер с таким IP уже существует — обновляет его.
     * Иначе добавляет новый сервер.
     */
    private fun saveServer(item: ServerItem)
    {
        val context = requireContext()
        val servers = ServersStorage.load(context)
        val existingIndex = servers.indexOfFirst { it.ip == item.ip }

        if (existingIndex >= 0) {
            servers[existingIndex] = item
        } else {
            servers.add(item)
        }

        ServersStorage.save(context, servers)
    }

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
        if (uri == null || !isAdded) return

        val edit = view?.findViewById<EditText>(R.id.ServerAddOpenVpn) ?: return
        val text = readOvpn(uri)

        if (text != null) {
            edit.setText(text)
        } else {
            edit.setText("")
            Toast.makeText(requireContext(), getString(R.string.server_create_load_file_error), Toast.LENGTH_LONG).show()
            edit.requestFocus()
        }
    }

    /**
     * Загружает и декодирует OVPN-конфигурацию из указанного URI.
     * Если файл закодирован в Base64 — декодирует его.
     * В случае ошибки возвращает null.
     */
    private fun readOvpn(uri: Uri): String?
    {
        return try {
            val resolver = requireContext().contentResolver
            resolver.openInputStream(uri)?.use { stream ->
                val rawText = stream.bufferedReader().readText()
                ServersParser.decodeConfig(rawText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
