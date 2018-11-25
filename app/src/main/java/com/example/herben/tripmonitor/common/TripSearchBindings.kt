package com.example.herben.tripmonitor.common

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.ui.searchTrip.SearchTripAdapter

@BindingAdapter("app:items")
fun RecyclerView.set(places: List<Trip>) {
    val ad = this.adapter as SearchTripAdapter
    ad.replaceData(places)
}