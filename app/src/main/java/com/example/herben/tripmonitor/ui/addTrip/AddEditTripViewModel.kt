package com.example.herben.tripmonitor.ui.addTrip

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.*
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User
import java.util.*

class AddEditTripViewModel : ViewModel(), DataSource.GetCallback<Trip>, DataSource.LoadCallback<User>{

    val name = ObservableField<String>()
    val body = ObservableField<String>()
    val dateFrom = ObservableField<Date>()
    val dateTo = ObservableField<Date>()
    val places = ObservableArrayList<String>()
    val dataLoading = ObservableBoolean(false)
    val userToAdd = ObservableField<String>()
    var users: ObservableList<User> = ObservableArrayList<User>()
    var usersHolder = UsersHolder()

    internal val snackbarMessage = SnackbarMessage()

    internal val tripUpdatedEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    internal val usersLoadedEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    private var mEntryId: String? = null

    private var isNewEntry: Boolean = false

    private var mIsDataLoaded = false

    private lateinit var repository: Repository

    private val currentDateTime: Date
        get() = Calendar.getInstance().time

    companion object{
        fun obtain(activity: FragmentActivity): AddEditTripViewModel {
            val vm = ViewModelProviders.of(activity).get(AddEditTripViewModel::class.java)
            vm.repository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    fun start(entryId: String?) {
        usersHolder.start(repository, usersLoadedEvent)
        if (dataLoading.get()) {
            // Already loading, ignore.
            return
        }
        mEntryId = entryId
        if (entryId == null) {
            // No need to populate, it's a new task
            isNewEntry = true
            return
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return
        }
        isNewEntry = false
        dataLoading.set(true)
        repository.getTrip(entryId, this)
    }

    override fun onLoaded(entity: Trip?) {
        name.set(entity!!.name)
        body.set(entity.body)
        dataLoading.set(false)
        mIsDataLoaded = true
        if(entity.users.isNotEmpty()){
            repository.getUsersFromList(entity.users, this)
        }
    }

    override fun onLoaded(entries: List<User>) {
        users.clear()
        users.addAll(entries)
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    // Called when clicking on fab.
    internal fun saveEntry() {
        val userIds = users.map { it.id }
        val user = repository.user.entity
        var trip = Trip(name.get(), body.get(), dateFrom.get(), dateTo.get(), user?.name, user?.id, userIds, places)

        if (trip.isEmpty()) {
            snackbarMessage.setValue(R.string.empty_trip_message)
            return
        }
        if (isNewEntry || mEntryId == null) {
            createTrip(trip)
        } else {
            trip = Trip(name.get(), body.get(),  dateFrom.get(), dateTo.get(),repository.user.entity?.name, repository.user.entity?.id, userIds, places, mEntryId!!)
            updateTrip(trip)
        }
    }

    fun addUser(){
        Log.i("TOMASZ", "Adding user ${userToAdd.get()}")
        for(user in usersHolder.users)
        {
            Log.i("TOMASZ", "user -> ${user.phoneNumber} || ${user.email}")
            val userData : String = userToAdd.get().orEmpty()
            val phoneNumber : String = user.phoneNumber
            val email : String = user.email
            if(phoneNumber == userData || email == userData){
                if(users.any{ o : User -> o.id == user.id }){
                    return
                }
                users.add(user)
            }
        }
        Log.i("TOMASZ", "User not added")
    }

    private fun createTrip(trip: Trip) {
        repository.saveTrip(trip)
        tripUpdatedEvent.call()
    }

    private fun updateTrip(trip: Trip) {
        if (isNewEntry) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        repository.saveTrip(trip)
        tripUpdatedEvent.call()
    }
}
