package com.example.herben.tripmonitor.data

import com.example.herben.tripmonitor.data.PostDataSource.GetPostCallback
import com.example.herben.tripmonitor.data.PostDataSource.LoadPostsCallback
import java.util.*


class PostRepository private constructor(private val postRemoteDataSource: PostDataSource,
                                            private val postLocalDataSource: PostDataSource) : PostDataSource {


    internal var mCachedTasks: MutableMap<String, Post> = LinkedHashMap()

    private var mCacheIsDirty = true


    override fun getEntries(callback: LoadPostsCallback) {
        checkNotNull(callback)

        if (!mCacheIsDirty) {
            callback.onEntriesLoaded(ArrayList<Post>(mCachedTasks.values))
            return
        }

        if (mCacheIsDirty) {
            getEntriesFromRemoteDataSource(callback)
        } else {
            postLocalDataSource.getEntries(object : LoadPostsCallback {
                override fun onEntriesLoaded(entries: List<Post>) {
                    refreshCache(entries)
                    callback.onEntriesLoaded(ArrayList<Post>(mCachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getEntriesFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun getEntry(entryId: String, callback: GetPostCallback) {
        checkNotNull(entryId)
        checkNotNull(callback)

        val cachedPost = getPostWithId(entryId)

        // Respond immediately with cache if available
        if (cachedPost != null) {
            callback.onPostLoaded(cachedPost)
            return
        }

        // Load from server/persisted if needed.
        // Is the task in the local data source? If not, query the network.
        postLocalDataSource.getEntry(entryId, object : GetPostCallback {
            override fun onPostLoaded(entry: Post?) {

                mCachedTasks[entry!!.id] = entry
                callback.onPostLoaded(entry)
            }

            override fun onDataNotAvailable() {

                postRemoteDataSource.getEntry(entryId, object : GetPostCallback {
                    override fun onPostLoaded(entry: Post?) {
                        if (entry == null) {
                            onDataNotAvailable()
                            return
                        }

                        mCachedTasks[entry.id] = entry
                        callback.onPostLoaded(entry)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun saveEntry(entry: Post) {
        checkNotNull(entry)
        postRemoteDataSource.saveEntry(entry)
        postLocalDataSource.saveEntry(entry)

        mCachedTasks[entry.id] = entry
    }

    override fun refreshEntries() {
        mCacheIsDirty = true
        mCachedTasks.clear()
    }


    override fun deleteAllEntries() {
        postRemoteDataSource.deleteAllEntries()
        postLocalDataSource.deleteAllEntries()

        mCachedTasks.clear()
    }

    override fun deleteEntry(entryId: String) {
        postRemoteDataSource.deleteEntry(checkNotNull(entryId))
        postLocalDataSource.deleteEntry(checkNotNull(entryId))
        mCachedTasks.remove(entryId)
    }

    private fun getEntriesFromRemoteDataSource(callback: LoadPostsCallback) {
        postRemoteDataSource.getEntries(object : LoadPostsCallback {
            override fun onEntriesLoaded(entries: List<Post>) {
                refreshCache(entries)
                refreshLocalDataSource(entries)
                callback.onEntriesLoaded(ArrayList<Post>(mCachedTasks.values))
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
        postLocalDataSource.deleteAllEntries()
        for (post in entries) {
            postLocalDataSource.saveEntry(post)
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
        private var INSTANCE: PostRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param postRemoteDataSource the backend data source
         * @param postLocalDataSource  the device storage data source
         * @return the [PostRepository] instance
         */
        fun getInstance(postRemoteDataSource: PostDataSource,
                        postLocalDataSource: PostDataSource): PostRepository {
            if (INSTANCE == null) {
                synchronized(PostRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = PostRepository(postRemoteDataSource, postLocalDataSource)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
