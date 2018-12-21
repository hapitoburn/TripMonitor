package com.example.herben.tripmonitor.ui.addTrip

import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.User

class UsersHolder : DataSource.LoadCallback<User>{
    var users = ArrayList<User>()
    lateinit var loadedEvent: SingleLiveEvent<Void>
    fun start(repository: Repository, usersLoadedEvent: SingleLiveEvent<Void>){
        loadedEvent = usersLoadedEvent
        repository.getUsers(this)
    }
    override fun onLoaded(entries: List<User>) {
        users.clear()
        users.addAll(entries)
        loadedEvent.call()
    }

    override fun onDataNotAvailable() {

    }

}