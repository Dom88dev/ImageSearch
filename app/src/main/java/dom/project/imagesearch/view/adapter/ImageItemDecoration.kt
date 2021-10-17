package dom.project.imagesearch.view.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
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

        val size3 = dpToPx(parent.context, 3f)
        val size4 = dpToPx(parent.context, 4f)
        val size2 = dpToPx(parent.context, 2f)

        when (position % 3) {
            0 -> {
                outRect.right = size4
            }
            1 -> {
                outRect.left = size2
                outRect.right = size2
            }
            2 -> {
                outRect.left = size4
            }
        }
        outRect.top = size3
        outRect.bottom = size3
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}