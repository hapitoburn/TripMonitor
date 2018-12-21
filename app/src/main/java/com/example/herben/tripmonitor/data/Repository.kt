package com.example.herben.tripmonitor.data

import android.util.Log
import com.example.herben.tripmonitor.data.DataSource.GetCallback
import com.example.herben.tripmonitor.data.DataSource.LoadCallback
import java.util.*


class Repository private constructor(private val remoteDataSource: DataSource,
                                     private val localDataSource: DataSource) : DataSource {
    internal var mCachedPosts = Cache<MutableMap<String, Post>>(LinkedHashMap())
    internal var mTrip = Cache<Trip>()
    internal var user = Cache<User>()

    fun loadUserId() {
        remoteDataSource.getUserId(
                object : GetCallback<User> {
                    override fun onLoaded(entity: User?) {
                        user.entity = entity
                        loadUser()
                    }
                    override fun onDataNotAvailable() {
                        Log.i("TOMASZ", "loadUserId failed")
                    }
                }
        )
    }

    fun loadUser(){
        getUser(user.entity!!.id,
                object : GetCallback<User>{
                    override fun onLoaded(entity: User?) {
                        user.entity = entity
                        user.isCacheDirty=false
                    }
                    override fun onDataNotAvailable() {
                    }
                })
    }

    override fun getUserId(callback: GetCallback<User>) {
        remoteDataSource.getUserId(callback)
    }

    override fun getUsersFromList(users: List<String>, callback: LoadCallback<User>) {
        remoteDataSource.getUsersFromList(users, callback)
    }

    override fun insertUser(userId: String) {
        Log.i("TOMASZ", "Insert user userId=$userId")
        remoteDataSource.insertUser(userId)
        localDataSource.insertUser(userId)
        user.entity = User(userId)
        user.isCacheDirty = false
    }

    override fun updateUser(name: String?, phoneNumber: String?, email: String?, userId: String) {
        remoteDataSource.updateUser(name, phoneNumber, email, userId)
        localDataSource.updateUser(name, phoneNumber, email, userId)
        user.entity.let {
            if(it!=null) {
                it.name = name.orEmpty()
                it.phoneNumber = phoneNumber.orEmpty()
                it.email = email.orEmpty()
                it.id = userId
            }
        }
    }


    override fun updateActiveTripInfo(userId: String, tripId: String) {
        user.isCacheDirty=false
        remoteDataSource.updateActiveTripInfo(userId, tripId)
        localDataSource.updateActiveTripInfo(userId, tripId)

        getUser(userId, callback = object : DataSource.GetCallback<User> {
            override fun onDataNotAvailable() {
            }
            override fun onLoaded(entity: User?) {
                user.entity = entity
                user.entity?.trip = tripId
                user.isCacheDirty=false
            }
        })
    }

    override fun getActiveTrip(userId: String, callback: GetCallback<Trip>) {
        checkNotNull(userId)
        checkNotNull(callback)

        // Respond immediately with cache if available
        if (!mTrip.isCacheDirty) {
            callback.onLoaded(mTrip.entity)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        localDataSource.getActiveTrip(userId, object : GetCallback<Trip> {
            override fun onLoaded(entity: Trip?) {
                mTrip.entity = entity
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getActiveTrip(userId, object : GetCallback<Trip> {
                    override fun onLoaded(entity: Trip?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mTrip.entity = entity
                        callback.onLoaded(entity)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }


    override fun getPosts(callback: LoadCallback<Post>) {
        checkNotNull(callback)

        if (!mCachedPosts.isCacheDirty) {
            callback.onLoaded(ArrayList<Post>(mCachedPosts.entity!!.values))
            return
        }

        localDataSource.getPosts(object : LoadCallback<Post> {
            override fun onLoaded(entries: List<Post>) {
                refreshPostsCache(entries)
                callback.onLoaded(ArrayList<Post>(mCachedPosts.entity!!.values))
            }

            override fun onDataNotAvailable() {
                getEntriesFromRemoteDataSource(callback)
            }
        })

    }

    override fun getPost(entryId: String, callback: GetCallback<Post>) {
        checkNotNull(entryId)
        checkNotNull(callback)

        val cachedPost = getPostWithId(entryId)

        // Respond immediately with cache if available
        if (cachedPost != null) {
            callback.onLoaded(cachedPost)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        localDataSource.getPost(entryId, object : GetCallback<Post> {
            override fun onLoaded(entity: Post?) {

                mCachedPosts.entity!![entity!!.id] = entity
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getPost(entryId, object : GetCallback<Post> {
                    override fun onLoaded(entity: Post?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mCachedPosts.entity!![entity.id] = entity
                        callback.onLoaded(entity)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun savePost(entry: Post) {
        checkNotNull(entry)
        remoteDataSource.savePost(entry)
        localDataSource.savePost(entry)

        mCachedPosts.entity!![entry.id] = entry
    }

    override fun refreshPosts() {
        mCachedPosts.isCacheDirty = true
        mCachedPosts.entity!!.clear()
    }


    override fun deleteAllPosts() {
        remoteDataSource.deleteAllPosts()
        localDataSource.deleteAllPosts()

        mCachedPosts.entity!!.clear()
    }

    override fun deletePost(entryId: String) {
        remoteDataSource.deletePost(checkNotNull(entryId))
        localDataSource.deletePost(checkNotNull(entryId))
        mCachedPosts.entity!!.remove(entryId)
    }


    private fun getEntriesFromRemoteDataSource(callback: LoadCallback<Post>) {
        remoteDataSource.getPosts(object : LoadCallback<Post> {
            override fun onLoaded(entries: List<Post>) {
                refreshPostsCache(entries)
                refreshLocalDataSource(entries)
                callback.onLoaded(ArrayList<Post>(mCachedPosts.entity!!.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun getUsersFromRemoteDataSource(callback: LoadCallback<User>) {
        remoteDataSource.getUsers(object : LoadCallback<User> {
            override fun onLoaded(entries: List<User>) {
                callback.onLoaded(entries)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun getTripsFromRemoteDataSource(callback: LoadCallback<Trip>) {
        remoteDataSource.getTrips(object : LoadCallback<Trip> {
            override fun onLoaded(entries: List<Trip>) {
                callback.onLoaded(emptyList())
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getUser(entryId: String, callback: GetCallback<User>) {
        checkNotNull(entryId)
        checkNotNull(callback)

        if(!user.isCacheDirty){
            return callback.onLoaded(user.entity)
        }

        localDataSource.getUser(entryId, object : GetCallback<User> {
            override fun onLoaded(entity: User?) {
                user.entity = entity
                user.isCacheDirty=false
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getUser(entryId, object : GetCallback<User> {
                    override fun onLoaded(entity: User?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }
                        user.entity = entity
                        user.isCacheDirty=false
                        callback.onLoaded(entity)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun getUsers(callback: LoadCallback<User>) {
        getUsersFromRemoteDataSource(callback)
    }

    override fun getTrip(entryId: String, callback: GetCallback<Trip>) {
        checkNotNull(entryId)
        checkNotNull(callback)

        // Respond immediately with cache if available
        if (mTrip.entity != null) {
            callback.onLoaded(mTrip.entity)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        localDataSource.getTrip(entryId, object : GetCallback<Trip> {
            override fun onLoaded(entity : Trip?) {

                mTrip.entity = entity
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getTrip(entryId, object : GetCallback<Trip> {
                    override fun onLoaded(entity: Trip?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mTrip.entity = entity
                        callback.onLoaded(entity)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun getTrips(callback: LoadCallback<Trip>) {
        getTripsFromRemoteDataSource(callback)
    }

    override fun deleteTrip(entryId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshTrips() {
        //
    }

    override fun saveTrip(trip: Trip) {
        checkNotNull(trip)
        remoteDataSource.saveTrip(trip)
        localDataSource.saveTrip(trip)
        mTrip.entity = trip
        user.entity?.trip = trip.id
    }

    private fun refreshPostsCache(entries: List<Post>) {
        mCachedPosts.entity?.clear()
        for (post in entries) {
            mCachedPosts.entity!![post.id] = post
        }
        mCachedPosts.isCacheDirty = false
    }

    private fun refreshLocalDataSource(entries: List<Post>) {
        localDataSource.deleteAllPosts()
        for (post in entries) {
            localDataSource.savePost(post)
        }
    }

    private fun getPostWithId(id: String): Post? {
        checkNotNull(id)
        return if (mCachedPosts.entity?.isEmpty()!!) {
            null
        } else {
            mCachedPosts.entity!![id]
        }
    }

    fun invalidate() {
        mCachedPosts.invalidate()
        mCachedPosts.entity = LinkedHashMap()
        user.invalidate()
        mTrip.invalidate()
    }

    companion object {

        @Volatile
        private var INSTANCE: Repository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param remoteDataSource the backend data source
         * @param localDataSource  the device storage data source
         * @return the [Repository] instance
         */
        fun getInstance(remoteDataSource: DataSource,
                        localDataSource: DataSource): Repository {
            if (INSTANCE == null) {
                synchronized(Repository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Repository(remoteDataSource, localDataSource)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
