package dom.project.imagesearch.utills

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import dom.project.imagesearch.R

// 스낵바 구현을 편하게 하기 위해 만든 함수
fun createSnackBarMessage(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    customLayoutRes: Int = R.layout.snackbar_custom,
    customDrawable: Drawable? = null,
    clickEvent: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(view, message, duration)
    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    val inflater = LayoutInflater.from(view.context)
    val customView = inflater.inflate(customLayoutRes, null, false)

    with(snackbarLayout) {
        removeAllViews()
        setPadding(0, 0, 0, 0)
        setBackgroundColor(Color.TRANSPARENT)
        addView(customView)
    }
    val content = customView.findViewById<TextView>(R.id.toast_content)
    content.text = message
    if (customDrawable != null) {
        content.setCompoundDrawablesWithIntrinsicBounds(null, customDrawable, null, null)
    }

    if (clickEvent != null) {
        customView.setOnClickListener {
            clickEvent.invoke()
            snackbar.dismiss()
        }
    }

    snackbar.show()
}