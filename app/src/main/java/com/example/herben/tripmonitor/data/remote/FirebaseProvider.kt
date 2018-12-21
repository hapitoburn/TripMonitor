package com.example.herben.tripmonitor.data.remote

import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User

interface FirebaseProvider {
    fun insertPost(entry: Post)

    fun deletePost(entryId: String)

    fun getAllPosts(): List<Post>

    fun getPostById(entryId: String): Post?

    fun getUserById(entryId: String, callback: DataSource.GetCallback<User>): User?
    fun getAllUsers(): List<User>
    fun insertUser(userId: String)

    fun getAllTrips(): List<Trip>

    fun getTripById(entryId: String, callback: DataSource.GetCallback<Trip>): Trip?

    fun insertTrip(entry: Trip)

    fun getActiveTrip(userId: String): Trip?
    fun updateActiveTrip(userId: String, tripId: String)
    fun updateUserInfo(name: String?, phoneNumber: String?, email: String?, userId: String)
    fun getUsersFromList(users: List<String>): List<User>
    fun getUserId(callback : DataSource.GetCallback<User>)

}