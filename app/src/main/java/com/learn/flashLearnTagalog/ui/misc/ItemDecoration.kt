package com.learn.flashLearnTagalog.ui.misc

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = spacing / 2

        //left side
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = spacing / 6
            outRect.right = spacing / 2
        }
        //right side
        else {
            outRect.left = spacing / 2
            outRect.right = spacing / 6
        }
        outRect.bottom = spacing / 2


    }

}