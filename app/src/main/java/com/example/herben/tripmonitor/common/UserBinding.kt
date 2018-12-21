package com.example.herben.tripmonitor.common

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.example.herben.tripmonitor.data.User
import com.example.herben.tripmonitor.ui.addTrip.UserAdapter

@BindingAdapter("app:items")
fun RecyclerView.set(users: List<User>) {
    val ad = this.adapter as UserAdapter
    ad.replaceData(users)
}
