package com.example.herben.tripmonitor.common

import android.content.Context
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.local.PostDatabase
import com.example.herben.tripmonitor.data.local.LocalDataSource
import com.example.herben.tripmonitor.data.remote.FirebaseDataSource
import com.example.herben.tripmonitor.data.remote.PostFirebaseDatabase

object Injection {

    fun provideRepository(context: Context): Repository {
        checkNotNull(context)
        val database = PostDatabase.getInstance(context)
        val firebaseDatabase = PostFirebaseDatabase.getInstance()
        return Repository.getInstance(FirebaseDataSource.getInstance(AppExecutors(), firebaseDatabase), LocalDataSource.getInstance(AppExecutors(), database))
    }
}
