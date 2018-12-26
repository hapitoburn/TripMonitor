package com.example.herben.tripmonitor.data

import java.util.*

data class TripPlaceInfo( var name: String? = "",
                          var date: Date? = Date(),
                          var details: String? = "")

{
    fun isEmpty() : Boolean{
        if (name.isNullOrEmpty())
            return true
        return false
    }
}