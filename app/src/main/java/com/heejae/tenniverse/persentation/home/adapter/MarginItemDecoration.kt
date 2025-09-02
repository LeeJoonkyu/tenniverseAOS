package com.heejae.tenniverse.persentation.home.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val verticalSize: Int = 0, private val horizontalSize: Int = 0) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = verticalSize
            }
            bottom = verticalSize
            right = horizontalSize
        }
    }
}