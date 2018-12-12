package com.example.herben.tripmonitor.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.util.*

@Entity(tableName = "users")
data class User(@ColumnInfo(name = "name") var name: String = "",
                @ColumnInfo(name = "phoneNumber") var phoneNumber: String = "",
                @ColumnInfo(name = "trip") var trip: String = "",
                @PrimaryKey @NonNull var id : String = UUID.randomUUID().toString()
)
