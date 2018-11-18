package com.example.herben.tripmonitor.common

import android.arch.persistence.room.TypeConverter
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList
import com.google.gson.Gson
import java.util.*


object ListTypeConverter {
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
        fun someObjectListToString(someObjects: List<String>): String {
            return gson.toJson(someObjects)
        }

}