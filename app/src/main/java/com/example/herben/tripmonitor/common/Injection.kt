package com.example.herben.tripmonitor.common

import android.content.Context
import com.example.herben.tripmonitor.data.PostRepository
import com.example.herben.tripmonitor.data.local.PostDatabase
import com.example.herben.tripmonitor.data.local.PostLocalDataSource
import com.example.herben.tripmonitor.data.remote.PostFirebaseDataSource
import com.example.herben.tripmonitor.data.remote.PostFirebaseDatabase

object Injection {

    fun provideRepository(context: Context): PostRepository {
        checkNotNull(context)
        val database = PostDatabase.getInstance(context)
        val firebaseDatabase = PostFirebaseDatabase.getInstance()
        return PostRepository.getInstance(PostFirebaseDataSource.getInstance(AppExecutors(), firebaseDatabase), PostLocalDataSource.getInstance(AppExecutors(), database.postDao()))
    }
}
