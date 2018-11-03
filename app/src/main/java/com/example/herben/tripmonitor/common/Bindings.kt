package com.example.herben.tripmonitor.common

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.ui.board.BoardAdapter
import com.example.herben.tripmonitor.ui.board.BoardViewModel

@BindingAdapter("android:onRefresh")
fun SwipeRefreshLayout.set( viewModel: BoardViewModel) {
    setOnRefreshListener {
        viewModel.loadEntries(true)
    }
}
@BindingAdapter("app:items")
fun RecyclerView.set(items: List<Post>) {
    val ad = this.adapter as BoardAdapter
    ad.replaceData(items)
}

