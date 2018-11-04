package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.herben.tripmonitor.common.DateConverter
import com.example.herben.tripmonitor.data.Post

@Database(entities = arrayOf(Post::class), version = 3)
@TypeConverters(DateConverter::class)
abstract class PostDatabase : RoomDatabase() {

    abstract fun posts(): PostDao
    abstract fun trips(): TripDao
    abstract fun users(): UserDao

    companion object {
        private var INSTANCE: PostDatabase? = null

        fun getInstance(context: Context): PostDatabase {
            if (INSTANCE == null) {
                synchronized(PostDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PostDatabase::class.java, "Posts.db")
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