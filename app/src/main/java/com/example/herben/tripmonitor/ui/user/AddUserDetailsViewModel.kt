package com.example.herben.tripmonitor.ui.user

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.herben.tripmonitor.AuthActivity
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.User

class AddUserDetailsViewModel : ViewModel(), DataSource.GetCallback<User> {
    companion object{
        fun obtain(activity: FragmentActivity): AddUserDetailsViewModel {
            val vm = ViewModelProviders.of(activity).get(AddUserDetailsViewModel::class.java)
            vm.postRepository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    val name = ObservableField<String>()
    val phoneNumber = ObservableField<String>()
    val email = ObservableField<String>()

    val dataLoading = ObservableBoolean(false)

    internal val snackbarMessage = SnackbarMessage()

    internal val userUpdatedEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    private var mEntryId: String? = null

    private var isNewEntry: Boolean = false

    private var mIsDataLoaded = false

    private lateinit var postRepository: Repository

    fun start() {
        mEntryId = AuthActivity.getUserId()
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
        postRepository.getUser(mEntryId!!, this)
    }

    override fun onLoaded(entity: User?) {
        name.set(entity!!.name)
        phoneNumber.set(entity.phoneNumber)
        email.set(entity.email)
        dataLoading.set(false)
        mIsDataLoaded = true
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    internal fun saveEntry() {
        Log.i("TOMASZ", "Saving userDetails entry ")
        if (phoneNumber.get() == null || name.get() == null) {
            snackbarMessage.setValue(R.string.empty_user_message)
            return
        }
        updateUser()
    }
    private fun updateUser() {
        Log.i("TOMASZ", "Update user details id $mEntryId")
        postRepository.updateUser( name.get(), phoneNumber.get(), email.get(), mEntryId ?: "")
        userUpdatedEvent.call()
    }
}
