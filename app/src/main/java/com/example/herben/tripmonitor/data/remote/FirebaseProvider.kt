package com.example.herben.tripmonitor.data.remote

import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip

interface FirebaseProvider {
    fun insertPost(entry: Post)

    fun deletePost(entryId: String)

    fun getAllPosts(): List<Post>

    fun getPostById(entryId: String): Post?

    fun getAllTrips(): List<Trip>

    fun getTripById(entryId: String): Trip?

    fun insertTrip(entry: Trip)
}