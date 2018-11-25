package com.example.herben.tripmonitor.ui.trip

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.graphics.drawable.Drawable
import android.support.v4.app.FragmentActivity
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.Trip
import java.util.*

class TripOverwiewViewModel (context: Application) : AndroidViewModel(context) {

    val empty = ObservableBoolean(false)

    val trip: ObservableField<Trip> = ObservableField()
    var name = ObservableField<String>()
    var body = ObservableField<String>()
    var dateFrom = ObservableField<Date>()
    var dateTo = ObservableField<Date>()
    val places: ObservableList<String> = ObservableArrayList<String>()
    var id = ObservableField<String>()

    val noTasksLabel = ObservableField<String>()

    val noTaskIconRes = ObservableField<Drawable>()

    val entriesAddViewVisible = ObservableBoolean()

    val isDataLoading = ObservableBoolean(false)
    val isDataLoadingError = ObservableBoolean(false)

    internal val editEntryEvent: SingleLiveEvent<String> = SingleLiveEvent()

    private var tripId: String = String()

    internal val snackbarMessage = SnackbarMessage()

    private var isNewEntry: Boolean = false

    private var mIsDataLoaded = false

    private lateinit var repository: Repository

    private val currentDateTime: Date
        get() = Calendar.getInstance().time

    companion object{
        fun obtain(activity: FragmentActivity, id : String): TripOverwiewViewModel {
            val vm = ViewModelProviders.of(activity).get(TripOverwiewViewModel::class.java)
            vm.repository = Injection.provideRepository(activity.applicationContext)
            vm.tripId = id
            return vm
        }
    }

    fun start() {
        loadEntry(false)
    }

    fun loadEntry(forceUpdate: Boolean) {
        loadEntry(forceUpdate, true)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadEntry(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            isDataLoading.set(true)
        }
        if (forceUpdate) {
            repository.refreshTrips()
        }

        repository.getTrip(tripId, object : DataSource.GetCallback<Trip> {
            override fun onLoaded(entity: Trip?) {
                if (showLoadingUI) {
                    isDataLoading.set(false)
                }
                isDataLoadingError.set(false)

                trip.set(entity)
                trip.notifyChange()
                empty.set(trip.get()!!.isEmpty())
            }

            override fun onDataNotAvailable() {
                isDataLoadingError.set(true)
            }
        })
    }
}
