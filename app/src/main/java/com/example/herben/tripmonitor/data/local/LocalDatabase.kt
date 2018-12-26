package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.herben.tripmonitor.common.DateConverter
import com.example.herben.tripmonitor.data.Alarm
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User

@Database(entities = arrayOf(Post::class, Trip::class, User::class, Alarm::class), version = 9)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun posts(): PostDao
    abstract fun trips(): TripDao
    abstract fun users(): UserDao
    abstract fun alarms(): AlarmDao

    companion object {
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            if (INSTANCE == null) {
                synchronized(LocalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            LocalDatabase::class.java, "tripMonitor.db")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}