package com.example.herben.tripmonitor.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.util.*

@Entity(tableName = "Alarms")
data class Alarm(@ColumnInfo(name = "name") var name: String? = "",
                 @ColumnInfo(name = "date") var date: Date?  = Date(),
                 @ColumnInfo(name = "tripId") var tripId: String? = "",
                 @PrimaryKey @NonNull var id: String = UUID.randomUUID().toString()
)
