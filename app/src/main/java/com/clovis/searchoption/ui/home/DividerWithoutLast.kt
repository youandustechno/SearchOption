package com.clovis.searchoption.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerWithoutLast(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {

    private val divider: Drawable? = run {
        val attrs = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        val d = attrs.getDrawable(0)
        attrs.recycle()
        d
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        // Stop before the last item
        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        // No offset for last item
        if (position == (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.bottom = divider?.intrinsicHeight ?: 0
        }
    }
}