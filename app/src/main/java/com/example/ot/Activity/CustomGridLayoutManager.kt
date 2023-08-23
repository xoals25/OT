package com.example.ot.Activity

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomGridLayoutManager(context: Context) : GridLayoutManager(context, 2) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        updateSpanCount()
        super.onLayoutChildren(recycler, state)
    }
    private fun updateSpanCount() {
        val colCount = if (childCount <= 1) {
            1
        } else {
            2
        }
        this.spanCount = colCount
    }
}