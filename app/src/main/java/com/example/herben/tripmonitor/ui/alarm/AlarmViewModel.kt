package com.example.herben.tripmonitor.ui.alarm

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.herben.tripmonitor.AuthActivity
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.Alarm
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.User
import java.util.*

class AlarmViewModel : ViewModel(), DataSource.LoadCallback<Alarm> {

    companion object{
        fun obtain(activity: FragmentActivity): AlarmViewModel {
            val vm = ViewModelProviders.of(activity).get(AlarmViewModel::class.java)
            vm.repository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    val alarms = ObservableArrayList<Alarm>()

    val dataLoading = ObservableBoolean(false)

    internal val snackbarMessage = SnackbarMessage()

    private var mEntryId: String? = null

    private var isNewEntry: Boolean = false

    private var mIsDataLoaded = false

    private lateinit var repository: Repository

    fun start() {
        mEntryId = AuthActivity.getTripId()
        if (dataLoading.get()) {
            // Already loading, ignore.
            return
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return
        }
        isNewEntry = false
        dataLoading.set(true)
        repository.getAlarms(mEntryId!!, this)
    }

    override fun onLoaded(entries: List<Alarm>) {
        alarms.addAll(entries)
        dataLoading.set(false)
        mIsDataLoaded = true
    }
    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    internal fun saveEntry() {
        Log.i("TOMASZ", "Saving alarm entry ")
        if (alarms.size > 0) {
            snackbarMessage.setValue(R.string.empty_alarms_message)
            return
        }
    }


}
