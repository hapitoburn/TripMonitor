package com.example.herben.tripmonitor.common

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.example.herben.tripmonitor.ui.trip.TripOverviewAdapter

@BindingAdapter("app:items")
fun RecyclerView.set(places: List<String>) {
    val ad = this.adapter as TripOverviewAdapter
    ad.replaceData(places)
}