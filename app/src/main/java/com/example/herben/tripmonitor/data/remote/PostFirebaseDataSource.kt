package com.example.herben.tripmonitor.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import com.example.herben.tripmonitor.MainActivity
import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.PostDataSource
import com.example.herben.tripmonitor.data.local.PostDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class PostFirebaseDataSource private constructor(appExecutors: AppExecutors, provider: PostFirebaseProvider) : PostDataSource {

    private val SERVICE_LATENCY_MS = 3000L

    private var usersUid: String? = null
    private var prefs: SharedPreferences? = null
    private var postProvider: PostFirebaseProvider = provider;
    private var appExecutors: AppExecutors = appExecutors;

    init {
        checkoutUser()
    }

    override fun getEntries(callback: PostDataSource.LoadPostsCallback) {
        val entryList = postProvider.getAllEntries()
        val handler = Handler()
        handler.postDelayed({ callback.onEntriesLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun getEntry(entryId: String, callback: PostDataSource.GetPostCallback) {
        val runnable = Runnable {
            val postEntry = postProvider.getEntryById(entryId)
            appExecutors.mainThread().execute {
                if (postEntry != null) {
                    callback.onPostLoaded(postEntry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun saveEntry(entry: Post) {
        checkNotNull(entry)
        val runnable = Runnable { postProvider.insertEntry(entry) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun deleteEntry(entryId: String) {
        val runnable = Runnable { postProvider.deleteEntry(entryId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun refreshEntries() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs
    }


    override fun deleteAllEntries() {
        //todo:add delete all entries logic here
    }

    private fun checkoutUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            usersUid = user.uid
        } else {
            usersUid = ""
        }
    }

    companion object {

        private var INSTANCE: PostFirebaseDataSource? = null

        fun getInstance(appExecutors: AppExecutors, postDatabase: PostFirebaseDatabase): PostFirebaseDataSource {
            if (INSTANCE == null) {
                INSTANCE = PostFirebaseDataSource(appExecutors, postDatabase)
            }
            return INSTANCE!!
        }
    }

    private fun setUpSharedPreferences() {
        val applicationContext = MainActivity.getContextOfApplication()
        val MY_PREFS_NAME = "UserUid"
        prefs = applicationContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
    }
}
