package com.example.herben.tripmonitor.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.support.annotation.NonNull
import com.example.herben.tripmonitor.common.ListTypeConverter
import java.util.*

@Entity(tableName = "trips")
data class Trip(@ColumnInfo(name = "name") var name: String? = "",
                @ColumnInfo(name = "body") var body: String? = "",
                @ColumnInfo(name = "dateFrom") var dateFrom: Date? = Date(),
                @ColumnInfo(name = "dateTo") var dateTo: Date? = Date(),
                @ColumnInfo(name = "places") var places: List<String> = emptyList(),
                @PrimaryKey @NonNull var id: String = UUID.randomUUID().toString()
)
{
    fun isEmpty() : Boolean{
        if (name.isNullOrBlank() || body.isNullOrBlank())
            return true
        return false
    }
}