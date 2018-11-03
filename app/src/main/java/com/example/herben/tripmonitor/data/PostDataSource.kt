package com.example.herben.tripmonitor.data

interface PostDataSource {

    interface LoadPostsCallback {
        fun onEntriesLoaded(entries: List<Post>)
        fun onDataNotAvailable()
    }

    interface GetPostCallback {
        fun onPostLoaded(post: Post?)
        fun onDataNotAvailable()
    }

    fun getEntries(callback: LoadPostsCallback)

    fun getEntry(entryId: String, callback: GetPostCallback)

    fun saveEntry(entry: Post)

    fun refreshEntries()

    fun deleteAllEntries()

    fun deleteEntry(entryId: String)
}
