package com.example.herben.tripmonitor.ui.addTrip

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.Trip
import java.util.*

class AddEditTripViewModel : ViewModel(), DataSource.GetCallback<Trip>{
    val name = ObservableField<String>()
    val body = ObservableField<String>()
    val dateFrom = ObservableField<Date>()
    val dateTo = ObservableField<Date>()
    val places = ObservableArrayList<String>()
    val dataLoading = ObservableBoolean(false)

    internal val snackbarMessage = SnackbarMessage()

    internal val tripUpdatedEvent: SingleLiveEvent<Void> = SingleLiveEvent()

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
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    // Called when clicking on fab.
    internal fun saveEntry() {
        var trip = Trip(name.get(), body.get(), dateFrom.get(), dateTo.get(), places)
        if (trip.isEmpty()) {
            snackbarMessage.setValue(R.string.empty_trip_message)
            return
        }
        if (isNewEntry || mEntryId == null) {
            createTrip(trip)
        } else {
            trip = Trip(name.get(), body.get(),  dateFrom.get(), dateTo.get(), places, mEntryId!!)
            updateTrip(trip)
        }
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
