package com.example.herben.tripmonitor.ui.contact

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.*
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.*
import java.util.*

class ContactsViewModel : ViewModel(), DataSource.GetCallback<Trip>, DataSource.LoadCallback<User>{

    val dataLoading = ObservableBoolean(false)
    var users: ObservableList<User> = ObservableArrayList<User>()

    internal val snackbarMessage = SnackbarMessage()

    private var mIsDataLoaded = false

    private lateinit var repository: Repository

    private val currentDateTime: Date
        get() = Calendar.getInstance().time

    companion object{
        fun obtain(activity: FragmentActivity): ContactsViewModel {
            val vm = ViewModelProviders.of(activity).get(ContactsViewModel::class.java)
            vm.repository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    fun start() {
        val entryId = repository.user.entity?.trip
        if(entryId.isNullOrEmpty())
            return
        dataLoading.set(true)
        repository.getTrip(entryId!!, this)
    }

    override fun onLoaded(entity: Trip?) {
        if(entity==null)
            return
        if(entity.users.isNotEmpty()){
            users.clear()
            repository.getUsersFromList(entity.users, this)
        }
    }

    override fun onLoaded(entries: List<User>) {
        users.clear()
        users.addAll(entries)
        dataLoading.set(false)
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
        mIsDataLoaded = true
    }

    // Called when clicking on fab.
    internal fun saveEntry() {

    }

    fun addUser(){

    }
}
