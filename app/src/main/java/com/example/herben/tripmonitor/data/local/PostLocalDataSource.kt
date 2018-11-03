package com.example.herben.tripmonitor.data.local

import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.PostDataSource


class PostLocalDataSource// Prevent direct instantiation.
private constructor(private val appExecutors: AppExecutors, private val posts: PostDao) : PostDataSource {

    override fun getEntries(callback: PostDataSource.LoadPostsCallback) {
        val runnable = Runnable {
            val entryList = posts.getEntries()
            appExecutors.mainThread().execute {
                if (entryList.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onEntriesLoaded(entryList)
                }
            }
        }
        appExecutors.diskIO().execute(runnable)
    }


    override fun getEntry(entryId: String, callback: PostDataSource.GetPostCallback) {
        val runnable = Runnable {
            val journalEntry = posts.getEntryById(entryId)

            appExecutors.mainThread().execute {
                if (journalEntry != null) {
                    callback.onPostLoaded(journalEntry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.diskIO().execute(runnable)
    }

    override fun saveEntry(entry: Post) {
        checkNotNull(entry)
        val runnable = Runnable { posts.insert(entry) }
        appExecutors.diskIO().execute(runnable)
    }

    override fun deleteEntry(entryId: String) {
        val deleteRunnable = Runnable { posts.deleteEntryById(entryId) }
        appExecutors.diskIO().execute(deleteRunnable)
    }

    override fun refreshEntries() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs

    }

    override fun deleteAllEntries() {
        //todo:add delete all entries logic here
    }

    companion object {

        @Volatile
        private var INSTANCE: PostLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, posts: PostDao): PostLocalDataSource {
            if (INSTANCE == null) {
                synchronized(PostLocalDataSource::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = PostLocalDataSource(appExecutors, posts)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
