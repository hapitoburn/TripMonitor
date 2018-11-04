package com.example.herben.tripmonitor.data

import com.example.herben.tripmonitor.data.DataSource.GetCallback
import com.example.herben.tripmonitor.data.DataSource.LoadCallback
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripViewModel
import java.util.*


class Repository private constructor(private val remoteDataSource: DataSource,
                                     private val localDataSource: DataSource) : DataSource {
    override fun saveTrip(trip: Trip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTrip(entryId: String, addEditTripViewModel: AddEditTripViewModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal var mCachedTasks: MutableMap<String, Post> = LinkedHashMap()

    private var mCacheIsDirty = true


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
                    override fun onLoaded(post: Post?) {
                        if (post == null) {
                            onDataNotAvailable()
                            return
                        }

                        mCachedTasks[post.id] = post
                        callback.onLoaded(post)
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
