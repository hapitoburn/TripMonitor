package com.example.herben.tripmonitor.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.util.*

@Entity(tableName = "trips")
data class Trip(@ColumnInfo(name = "name") var name: String? = "",
                @ColumnInfo(name = "body") var body: String? = "",
                @ColumnInfo(name = "dateFrom") var dateFrom: Date? = Date(),
                @ColumnInfo(name = "dateTo") var dateTo: Date? = Date(),
                @PrimaryKey @NonNull var id: String = UUID.randomUUID().toString()
)
{
    fun isEmpty() : Boolean{
        if (name.isNullOrBlank() || body.isNullOrBlank() || dateFrom == null || dateTo == null)
            return true
        return false
    }
}