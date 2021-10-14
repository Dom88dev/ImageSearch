package dom.project.imagesearch.view.adapter

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ImageItemDecoration : RecyclerView.ItemDecoration() {

    // 아이템 간견 조정
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val size5 = dpToPx(parent.context, 5f)

        if (position < 3) {
            outRect.top = size5
        }

        when (position % 3) {
            1 -> {
                outRect.left = size5
            }
            2 -> {
                outRect.left = size5
                outRect.right = size5
            }
            0 -> {
                outRect.right = size5
            }
        }
        outRect.bottom = size5
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}