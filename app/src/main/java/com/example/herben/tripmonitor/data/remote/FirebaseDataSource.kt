package com.example.herben.tripmonitor.data.remote

import android.content.SharedPreferences
import android.os.Handler
import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User
import com.google.firebase.auth.FirebaseAuth

class FirebaseDataSource private constructor(private var appExecutors: AppExecutors, private var provider: FirebaseProvider) : DataSource {
    override fun getUserId(callback: DataSource.GetCallback<User>) {

        val runnable = Runnable {
            appExecutors.mainThread().execute {
                provider.getUserId(callback)
            }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getUsersFromList(users: List<String>, callback: DataSource.LoadCallback<User>) {
        val entryList = provider.getUsersFromList(users)
        val handler = Handler()
        handler.postDelayed({ callback.onLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun updateUser(name: String?, phoneNumber: String?, email: String?, userId: String) {
        val runnable = Runnable { provider.updateUserInfo(name, phoneNumber, email, userId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun insertUser(userId: String) {
        val runnable = Runnable { provider.insertUser(userId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun updateActiveTripInfo(userId: String, tripId: String) {
        val runnable = Runnable { provider.updateActiveTrip(userId, tripId) }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getActiveTrip(userId: String, callback: DataSource.GetCallback<Trip>) {
        val runnable = Runnable {
            val entry = provider.getActiveTrip(userId)
            appExecutors.mainThread().execute {
                if (entry != null) {
                    callback.onLoaded(entry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getUser(entryId: String, callback: DataSource.GetCallback<User>) {
        val runnable = Runnable {
            val entry = provider.getUserById(entryId, callback)
            appExecutors.mainThread().execute {
                if (entry != null) {
                    callback.onLoaded(entry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getUsers(callback: DataSource.LoadCallback<User>) {
        val entryList = provider.getAllUsers()
        val handler = Handler()
        handler.postDelayed({ callback.onLoaded(entryList) }, SERVICE_LATENCY_MS)
    }

    override fun getTrip(entryId: String, callback: DataSource.GetCallback<Trip>) {
        val runnable = Runnable {
            appExecutors.mainThread().execute {
                provider.getTripById(entryId, callback)
            }
        }
        appExecutors.networkIO().execute(runnable)
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
}
