package com.example.herben.tripmonitor.data

interface DataSource {
    interface LoadCallback<E> {
        fun onLoaded(entries: List<E>)
        fun onDataNotAvailable()
    }

    interface GetCallback<E> {
        fun onLoaded(entity: E?)
        fun onDataNotAvailable()
    }
    fun getActiveTrip(userId: String, callback: GetCallback<Trip>)
    fun updateActiveTripInfo(userId: String, tripId: String)
    fun getPost(entryId: String, callback: GetCallback<Post>)
    fun getPosts(tripId: String, callback: LoadCallback<Post>)
    fun savePost(entry: Post)
    fun refreshPosts()
    fun deleteAllPosts()
    fun deletePost(entryId: String)
    fun getUser(entryId: String, callback: GetCallback<User>)
    fun getUserId(callback: GetCallback<User>)
    fun getUsers(callback: LoadCallback<User>)

    fun saveTrip(trip: Trip)
    fun getTrip(entryId: String, callback: GetCallback<Trip>)
    fun getTrips(callback: LoadCallback<Trip>)
    fun deleteTrip(entryId: String)
    fun refreshTrips()

    fun insertUser(userId: String)
    fun updateUser(name: String?, phoneNumber: String?, email: String?, userId: String)
    fun getUsersFromList(users: List<String>,  callback: LoadCallback<User>)

    fun getAlarms(tripId: String, callback: LoadCallback<Alarm>)
    fun insertAlarm(alarm: Alarm)

}
