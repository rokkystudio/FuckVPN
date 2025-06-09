package fuck.system.vpn.servers.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import fuck.system.vpn.R

class ServerActionDialog : DialogFragment()
{
    companion object
    {
        const val TAG = "MenuServerDialog"
        private const val KEY_FAVORITE = "favorite"
        private const val KEY_POSITION = "position"

        fun newInstance(isFavorite: Boolean, position: Int): ServerActionDialog
        {
            val dialog = ServerActionDialog()
            dialog.arguments = Bundle().apply {
                putBoolean(KEY_FAVORITE, isFavorite)
                putInt(KEY_POSITION, position)
            }
            return dialog
        }

        const val ACTION_CONNECT = "connect"
        const val ACTION_FAVORITE = "favorite"
        const val ACTION_DELETE = "delete"
        const val RESULT_KEY = "menu_dialog_result"
        const val EXTRA_ACTION = "action"
        const val EXTRA_POSITION = "position"
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val isFavorite = requireArguments().getBoolean(KEY_FAVORITE)
        val position = requireArguments().getInt(KEY_POSITION)

        val view = layoutInflater.inflate(R.layout.dialog_server_action, null, false)

        val btnConnect = view.findViewById<ImageButton>(R.id.btnConnect)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_star_filled
            else R.drawable.ic_star_outline
        )
        btnFavorite.contentDescription =
            if (isFavorite) getString(R.string.server_action_remove_favorite)
            else getString(R.string.server_action_add_favorite)

        btnConnect.setOnClickListener {
            sendResult(ACTION_CONNECT, position)
            dismiss()
        }
        btnFavorite.setOnClickListener {
            sendResult(ACTION_FAVORITE, position)
            dismiss()
        }
        btnDelete.setOnClickListener {
            sendResult(ACTION_DELETE, position)
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun sendResult(action: String, position: Int)
    {
        parentFragmentManager.setFragmentResult(
            RESULT_KEY,
            Bundle().apply {
                putString(EXTRA_ACTION, action)
                putInt(EXTRA_POSITION, position)
            }
        )
    }
}
