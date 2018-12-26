package com.example.herben.tripmonitor.data.remote

import com.example.herben.tripmonitor.data.*

interface FirebaseProvider {
    fun insertPost(entry: Post)

    fun deletePost(entryId: String)

    fun getAllPosts(tripId: String, callback: DataSource.LoadCallback<Post>)

    fun getPostById(entryId: String): Post?

    fun getUserById(entryId: String, callback: DataSource.GetCallback<User>)
    fun getAllUsers(): List<User>
    fun insertUser(userId: String)

    fun getAllTrips(): List<Trip>

    fun getTripById(entryId: String, callback: DataSource.GetCallback<Trip>)

    fun insertTrip(entry: Trip)

    fun getActiveTrip(userId: String): Trip?
    fun updateActiveTrip(userId: String, tripId: String)
    fun updateUserInfo(name: String?, phoneNumber: String?, email: String?, userId: String)
    fun getUsersFromList(users: List<String>, callback: DataSource.LoadCallback<User>)
    fun getUserId(callback : DataSource.GetCallback<User>)

    fun getAlarms(tripId: String, callback: DataSource.LoadCallback<Alarm>)
    fun insertAlarm(alarm: Alarm)

}