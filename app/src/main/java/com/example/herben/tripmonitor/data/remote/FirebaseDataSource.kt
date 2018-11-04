package com.example.herben.tripmonitor.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import com.example.herben.tripmonitor.MainActivity
import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripViewModel
import com.google.firebase.auth.FirebaseAuth

class FirebaseDataSource private constructor(appExecutors: AppExecutors, provider: PostFirebaseProvider) : DataSource {
    override fun saveTrip(trip: Trip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTrip(entryId: String, addEditTripViewModel: AddEditTripViewModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val SERVICE_LATENCY_MS = 3000L

    private var usersUid: String? = null
    private var prefs: SharedPreferences? = null
    private var postProvider: PostFirebaseProvider = provider;
    private var appExecutors: AppExecutors = appExecutors;

    init {
        checkoutUser()
    }

    override fun getPosts(callback: DataSource.LoadCallback<Post>) {
        val entryList = postProvider.getAllEntries()
        val handler = Handler()
        handler.postDelayed({ callback.onLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun getPost(entryId: String, callback: DataSource.GetCallback<Post>) {
        val runnable = Runnable {
            val postEntry = postProvider.getEntryById(entryId)
            appExecutors.mainThread().execute {
                if (postEntry != null) {
                    callback.onLoaded(postEntry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun savePost(entry: Post) {
        checkNotNull(entry)
        val runnable = Runnable { postProvider.insertEntry(entry) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun deletePost(entryId: String) {
        val runnable = Runnable { postProvider.deleteEntry(entryId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun refreshPosts() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs
    }


    override fun deleteAllPosts() {
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

        private var INSTANCE: FirebaseDataSource? = null

        fun getInstance(appExecutors: AppExecutors, postDatabase: PostFirebaseDatabase): FirebaseDataSource {
            if (INSTANCE == null) {
                INSTANCE = FirebaseDataSource(appExecutors, postDatabase)
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
