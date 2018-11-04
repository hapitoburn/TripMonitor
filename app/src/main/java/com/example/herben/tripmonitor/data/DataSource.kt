package com.example.herben.tripmonitor.data

import com.example.herben.tripmonitor.ui.addTrip.AddEditTripViewModel

interface DataSource {

    interface LoadCallback<E> {
        fun onLoaded(entries: List<E>)
        fun onDataNotAvailable()
    }

    interface GetCallback<E> {
        fun onLoaded(entity: E?)
        fun onDataNotAvailable()
    }

    fun getPosts(callback: LoadCallback<Post>)
    fun getPost(entryId: String, callback: GetCallback<Post>)

    fun savePost(entry: Post)

    fun refreshPosts()

    fun deleteAllPosts()

    fun deletePost(entryId: String)

    fun saveTrip(trip: Trip)
    fun getTrip(entryId: String, addEditTripViewModel: AddEditTripViewModel)
}
