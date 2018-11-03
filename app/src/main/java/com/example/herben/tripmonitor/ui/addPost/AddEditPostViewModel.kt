package com.example.herben.tripmonitor.ui.addPost

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SingleLiveEvent
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.PostDataSource
import com.example.herben.tripmonitor.data.PostRepository
import java.util.*

class AddEditPostViewModel(application: Application) : AndroidViewModel(application), PostDataSource.GetPostCallback {

    val title = ObservableField<String>()

    val body = ObservableField<String>()

    val date = ObservableField<Date>()

    val dataLoading = ObservableBoolean(false)

    internal val snackbarMessage = SnackbarMessage()

    internal val postUpdatedEvent: SingleLiveEvent<Void> = SingleLiveEvent()

    private var mEntryId: String? = null

    private var isNewEntry: Boolean = false

    private var mIsDataLoaded = false

    private lateinit var postRepository: PostRepository

    private val currentDateTime: Date
        get() = Calendar.getInstance().time

    companion object{
        fun obtain(activity: FragmentActivity): AddEditPostViewModel {
            var vm = ViewModelProviders.of(activity).get(AddEditPostViewModel::class.java)
            vm.postRepository = Injection.provideRepository(activity.applicationContext)
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
        postRepository.getEntry(entryId, this)
    }

    override fun onPostLoaded(post: Post?) {
        title.set(post!!.title)
        body.set(post.body)
        dataLoading.set(false)
        mIsDataLoaded = true
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    // Called when clicking on fab.
    internal fun saveEntry() {
        var entry = Post(title.get(), body.get(), currentDateTime)
        if (entry.title.isEmpty() || entry.body.isEmpty()) {
            snackbarMessage.setValue(R.string.empty_post_message)
            return
        }
        if (isNewEntry || mEntryId == null) {
            createTask(entry)
        } else {
            entry = Post(title.get(), body.get(),  currentDateTime, mEntryId!!)
            updateTask(entry)
        }
    }

    private fun createTask(newEntry: Post) {
        postRepository.saveEntry(newEntry)
        postUpdatedEvent.call()
    }

    private fun updateTask(entry: Post) {
        if (isNewEntry) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        postRepository.saveEntry(entry)
        postUpdatedEvent.call()
    }
}