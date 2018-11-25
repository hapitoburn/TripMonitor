package com.example.herben.tripmonitor.common

import android.content.Context
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.local.LocalDataSource
import com.example.herben.tripmonitor.data.local.LocalDatabase
import com.example.herben.tripmonitor.data.remote.FirebaseDataSource
import com.example.herben.tripmonitor.data.remote.RemoteDatabase

object Injection {

    fun provideRepository(context: Context): Repository {
        checkNotNull(context)
        val database = LocalDatabase.getInstance(context)
        val firebaseDatabase = RemoteDatabase.getInstance()
        return Repository.getInstance(FirebaseDataSource.getInstance(AppExecutors(), firebaseDatabase), LocalDataSource.getInstance(AppExecutors(), database))
    }
}
