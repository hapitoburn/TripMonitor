package com.example.herben.tripmonitor.data

import com.example.herben.tripmonitor.AuthActivity
import com.example.herben.tripmonitor.data.DataSource.GetCallback
import com.example.herben.tripmonitor.data.DataSource.LoadCallback
import java.util.*


class Repository private constructor(private val remoteDataSource: DataSource,
                                     private val localDataSource: DataSource) : DataSource {
    override fun insertUser(userId: String) {
        remoteDataSource.insertUser(userId)
        localDataSource.insertUser(userId)
        user = User("", "", "", userId)
    }

    override fun updateUser(name: String, phoneNumber: String, userId: String) {
        remoteDataSource.updateUser(name, phoneNumber, userId)
       //localDataSource.updateUser(name, phoneNumber, userId)

        val uid = AuthActivity.getUserUid()
        uid?.let {
            getUser(it, callback = object : DataSource.GetCallback<User> {
                override fun onDataNotAvailable() {
                }
                override fun onLoaded(entity: User?) {
                    user = entity
                }
            })
        }
    }

    internal var mCachedTasks: MutableMap<String, Post> = LinkedHashMap()
    internal var mTrip: Trip? = null
    internal var user: User? = null
    private var mCacheIsDirty = true

    override fun updateActiveTripInfo(userId: String, tripId: String) {
        remoteDataSource.updateActiveTripInfo(userId, tripId)
        localDataSource.updateActiveTripInfo(userId, tripId)

        getTrip(tripId, callback = object : DataSource.GetCallback<Trip> {
            override fun onDataNotAvailable() {
            }
            override fun onLoaded(entity: Trip?) {
                mTrip = entity
            }
        })
    }

    override fun getActiveTrip(userId: String, callback: GetCallback<Trip>) {
        checkNotNull(userId)
        checkNotNull(callback)

        // Respond immediately with cache if available
        if (mTrip != null) {
            callback.onLoaded(mTrip)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        localDataSource.getActiveTrip(userId, object : GetCallback<Trip> {
            override fun onLoaded(entity: Trip?) {

                mTrip = entity
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getActiveTrip(userId, object : GetCallback<Trip> {
                    override fun onLoaded(entity: Trip?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mTrip = entity
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

        if (!mCacheIsDirty) {
            callback.onLoaded(ArrayList<Post>(mCachedTasks.values))
            return
        }

        if (mCacheIsDirty) {
            getEntriesFromRemoteDataSource(callback)
        } else {
            localDataSource.getPosts(object : LoadCallback<Post> {
                override fun onLoaded(entries: List<Post>) {
                    refreshCache(entries)
                    callback.onLoaded(ArrayList<Post>(mCachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getEntriesFromRemoteDataSource(callback)
                }
            })
        }
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
            override fun onLoaded(post: Post?) {

                mCachedTasks[post!!.id] = post
                callback.onLoaded(post)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getPost(entryId, object : GetCallback<Post> {
                    override fun onLoaded(entity: Post?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mCachedTasks[entity.id] = entity
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

        mCachedTasks[entry.id] = entry
    }

    override fun refreshPosts() {
        mCacheIsDirty = true
        mCachedTasks.clear()
    }


    override fun deleteAllPosts() {
        remoteDataSource.deleteAllPosts()
        localDataSource.deleteAllPosts()

        mCachedTasks.clear()
    }

    override fun deletePost(entryId: String) {
        remoteDataSource.deletePost(checkNotNull(entryId))
        localDataSource.deletePost(checkNotNull(entryId))
        mCachedTasks.remove(entryId)
    }


    private fun getEntriesFromRemoteDataSource(callback: LoadCallback<Post>) {
        remoteDataSource.getPosts(object : LoadCallback<Post> {
            override fun onLoaded(entries: List<Post>) {
                refreshCache(entries)
                refreshLocalDataSource(entries)
                callback.onLoaded(ArrayList<Post>(mCachedTasks.values))
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

        localDataSource.getUser(entryId, object : GetCallback<User> {
            override fun onLoaded(entity: User?) {
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getUser(entryId, object : GetCallback<User> {
                    override fun onLoaded(entity: User?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }
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

        val cached = mTrip

        // Respond immediately with cache if available
        if (cached != null) {
            callback.onLoaded(cached)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        localDataSource.getTrip(entryId, object : GetCallback<Trip> {
            override fun onLoaded(entity : Trip?) {

                mTrip = entity
                callback.onLoaded(entity)
            }

            override fun onDataNotAvailable() {

                remoteDataSource.getTrip(entryId, object : GetCallback<Trip> {
                    override fun onLoaded(entity: Trip?) {
                        if (entity == null) {
                            onDataNotAvailable()
                            return
                        }

                        mTrip = entity
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveTrip(trip: Trip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun refreshCache(entries: List<Post>) {
        mCachedTasks.clear()
        for (post in entries) {
            mCachedTasks[post.id] = post
        }
        mCacheIsDirty = false
    }

    private fun refreshLocalDataSource(entries: List<Post>) {
        localDataSource.deleteAllPosts()
        for (post in entries) {
            localDataSource.savePost(post)
        }
    }

    private fun getPostWithId(id: String): Post? {
        checkNotNull(id)
        return if ( mCachedTasks.isEmpty()) {
            null
        } else {
            mCachedTasks[id]
        }
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
