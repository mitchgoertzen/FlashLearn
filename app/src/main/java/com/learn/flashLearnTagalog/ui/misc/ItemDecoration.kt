package com.learn.flashLearnTagalog.ui.misc

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView


class ItemDecoration(
    private val windowManager: WindowManager,
    private val res: Resources
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


        val dpi = res.displayMetrics.density

        Log.d(TAG, "dpi: $dpi")

        val displayMetrics = DisplayMetrics()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            width = bounds.width()

        } else {
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