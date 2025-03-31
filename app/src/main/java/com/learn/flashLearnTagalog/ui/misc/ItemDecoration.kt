package com.learn.flashLearnTagalog.ui.misc

import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView


class ItemDecoration(
    private val windowManager: WindowManager
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        var verticalSpacing: Int
        var horizontalSpacing: Int
        val width: Int


        var dpi = 0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                dpi = windowManager.currentWindowMetrics.density
            }

            width = bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.getDefaultDisplay().getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
        }

        val lessonSize = 169 * dpi

        horizontalSpacing = ((width - (lessonSize * 2)) / 3).toInt()
        verticalSpacing = (lessonSize / 20).toInt()

        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing / 2

        //left side
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = horizontalSpacing
            outRect.right = horizontalSpacing / 2
        }
        //right side
        else {
            outRect.left = horizontalSpacing / 2
            outRect.right = horizontalSpacing
        }


    }

}