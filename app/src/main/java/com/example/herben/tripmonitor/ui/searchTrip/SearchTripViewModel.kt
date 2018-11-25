package com.example.herben.tripmonitor.ui.searchTrip

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.graphics.drawable.Drawable
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.Trip
import java.util.ArrayList

class SearchTripViewModel : ViewModel() {
    companion object{
        fun obtain(activity: FragmentActivity): SearchTripViewModel{
            var vm = ViewModelProviders.of(activity).get(SearchTripViewModel::class.java)
            vm.repository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    val dataLoading = ObservableBoolean(false)

    private val mIsDataLoadingError = ObservableBoolean(false)

    val empty = ObservableBoolean(false)

    val trips: ObservableList<Trip> = ObservableArrayList<Trip>()

    private val mOpenEntryEvent = SingleLiveEvent<String>()

    private val newEntryEvent = SingleLiveEvent<Void>()

    val noTasksLabel = ObservableField<String>()

    val noTaskIconRes = ObservableField<Drawable>()

    val entriesAddViewVisible = ObservableBoolean()

    private lateinit var repository: Repository

    fun start() {
        loadEntries(false)
    }

    fun loadEntries(forceUpdate: Boolean) {
        loadEntries(forceUpdate, true)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadEntries(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            dataLoading.set(true)
        }
        if (forceUpdate) {
            repository.refreshTrips()
        }

        repository.getTrips(object : DataSource.LoadCallback<Trip> {
            override fun onLoaded(entries: List<Trip>) {
                val entriesToShow = ArrayList<Trip>(entries)
                if (showLoadingUI) {
                    dataLoading.set(false)
                }
                mIsDataLoadingError.set(false)
                trips.clear()
                trips.addAll(entriesToShow)
                empty.set(trips.isEmpty())
                Log.i("TOMASZ", trips.count().toString())
            }

            override fun onDataNotAvailable() {
                mIsDataLoadingError.set(true)
            }
        })
    }

    internal fun getOpenEntryEvent(): SingleLiveEvent<String> {
        return mOpenEntryEvent
    }

    internal fun getNewEntryEvent(): SingleLiveEvent<Void> {
        return newEntryEvent
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    fun addNewEntry() {
        newEntryEvent.call()
    }

    internal fun handleActivityResult(requestCode: Int, resultCode: Int) {
        /*if (AddEditEntryActivity.REQUEST_CODE === requestCode) {
            when (resultCode) {
                EntryDetailActivity.EDIT_RESULT_OK -> mSnackbarText.setValue(R.string.successfully_saved_entry_message)
                AddEditEntryActivity.ADD_EDIT_RESULT_OK -> mSnackbarText.setValue(R.string.successfully_added_entry_message)
                EntryDetailActivity.DELETE_RESULT_OK -> mSnackbarText.setValue(R.string.successfully_deleted_entry_message)
            }
        }*/
    }

    fun clearCachedEntries() {
        repository.refreshPosts()
    }

}
