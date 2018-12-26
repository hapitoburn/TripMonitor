package com.example.herben.tripmonitor.common

import android.arch.persistence.room.TypeConverter
import com.example.herben.tripmonitor.data.TripPlaceInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return when (timestamp) {
            null -> null
            else -> Date(timestamp)
        }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
    internal var gson = Gson()
    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<String> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {

        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringTotripPlaceInfoObjecList(data: String?): List<TripPlaceInfo> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<TripPlaceInfo>>() {

        }.type

        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun stringObjectListToString(someObjects: List<String>): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun tripPlaceInfoObjectListToString(someObjects: List<TripPlaceInfo>): String {
        return gson.toJson(someObjects)
    }

}