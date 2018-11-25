package com.example.herben.tripmonitor.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import com.example.herben.tripmonitor.MainActivity
import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Trip
import com.google.firebase.auth.FirebaseAuth

class FirebaseDataSource private constructor(private var appExecutors: AppExecutors, private var provider: FirebaseProvider) : DataSource {
    override fun getTrip(entryId: String, callback: DataSource.GetCallback<Trip>) {
    }

    override fun deleteTrip(entryId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val SERVICE_LATENCY_MS = 3000L

    private var usersUid: String? = null
    private var prefs: SharedPreferences? = null

    init {
        checkoutUser()
    }

    override fun saveTrip(trip: Trip) {
        checkNotNull(trip)
        val runnable = Runnable { provider.insertTrip(trip) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getTrips(callback: DataSource.LoadCallback<Trip>) {
        val entryList = provider.getAllTrips()
        val handler = Handler()
        handler.postDelayed({ callback.onLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun getPosts(callback: DataSource.LoadCallback<Post>) {
        val entryList = provider.getAllPosts()
        val handler = Handler()
        handler.postDelayed({ callback.onLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun getPost(entryId: String, callback: DataSource.GetCallback<Post>) {
        val runnable = Runnable {
            val postEntry = provider.getPostById(entryId)
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
        val runnable = Runnable { provider.insertPost(entry) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun deletePost(entryId: String) {
        val runnable = Runnable { provider.deletePost(entryId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun refreshPosts() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs
    }

    override fun refreshTrips() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        fun getInstance(appExecutors: AppExecutors, localDatabase: RemoteDatabase): FirebaseDataSource {
            if (INSTANCE == null) {
                INSTANCE = FirebaseDataSource(appExecutors, localDatabase)
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
