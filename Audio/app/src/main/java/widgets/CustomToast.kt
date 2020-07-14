package widgets

import android.R.id.message
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.example.deezer.R


class CustomToast(context: Context, infos: String) {
    private val ctx = context
    private val data = infos
    private val ERROR_BACKGROUND_COLOR = ctx.getColor(R.color.error_toast_color)
    private val SUCCESS_BACKGROUND_COLOR = ctx.getColor(R.color.success_toast_color)

    fun showCustomToast(type: Int) {
        val toast = Toast.makeText(ctx, data, Toast.LENGTH_LONG)
        val view = toast.view

        if (type == 0) {
            view.background.setTint(ERROR_BACKGROUND_COLOR)
        } else {
            view.background.setTint(SUCCESS_BACKGROUND_COLOR)
        }
        val text = view.findViewById<TextView>(message)
        text.textSize = 15F
        text.setTextColor(ctx.getColor(R.color.dark))

        toast.show()
    }
}