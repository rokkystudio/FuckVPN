package fuck.system.vpn.servers.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R
import fuck.system.vpn.servers.server.ServerItem

class MenuServerDialog : DialogFragment()
{
    companion object
    {
        const val TAG = "MenuServerDialog"
        private const val KEY_SERVER = "server"
        private const val KEY_POSITION = "position"

        fun newInstance(server: ServerItem, position: Int): MenuServerDialog
        {
            val dialog = MenuServerDialog()
            dialog.arguments = Bundle().apply
            {
                putParcelable(KEY_SERVER, server)
                putInt(KEY_POSITION, position)
            }
            return dialog
        }

        // Коды действий
        const val ACTION_CONNECT = "connect"
        const val ACTION_FAVORITE = "favorite"
        const val ACTION_DELETE = "delete"

        // Для передачи результата
        const val RESULT_KEY = "menu_dialog_result"
        const val EXTRA_ACTION = "action"
        const val EXTRA_SERVER = "server"
        const val EXTRA_POSITION = "position"
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val server = requireArguments().getParcelable<ServerItem>(KEY_SERVER)!!
        val position = requireArguments().getInt(KEY_POSITION)

        val view = layoutInflater.inflate(R.layout.dialog_menu_server, null, false)

        val btnConnect = view.findViewById<ImageButton>(R.id.btnConnect)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        // Выбор иконки для избранного
        btnFavorite.setImageResource(
            if (server.favorite) R.drawable.ic_servers_star_outline
            else R.drawable.ic_servers_star_filled
        )
        btnFavorite.contentDescription =
            if (server.favorite) getString(R.string.menu_server_remove_favorite)
            else getString(R.string.menu_server_add_favorite)

        btnConnect.setOnClickListener {
            sendResult(ACTION_CONNECT, server, position)
            dismiss()
        }

        btnFavorite.setOnClickListener {
            sendResult(ACTION_FAVORITE, server, position)
            dismiss()
        }

        btnDelete.setOnClickListener {
            sendResult(ACTION_DELETE, server, position)
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        // Сделать диалог отменяемым только кликом вне и кнопкой "назад"
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun sendResult(action: String, server: ServerItem, position: Int)
    {
        parentFragmentManager.setFragmentResult(
            RESULT_KEY,
            Bundle().apply
            {
                putString(EXTRA_ACTION, action)
                putParcelable(EXTRA_SERVER, server)
                putInt(EXTRA_POSITION, position)
            }
        )
    }
}
