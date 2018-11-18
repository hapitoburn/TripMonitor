package com.example.herben.tripmonitor.ui.board

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
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
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Repository
import java.util.ArrayList

class BoardViewModel
(context: Application) : AndroidViewModel(context) {

    companion object{
        fun obtain(activity: FragmentActivity): BoardViewModel{
            var vm = ViewModelProviders.of(activity).get(BoardViewModel::class.java)
            vm.postRepository = Injection.provideRepository(activity.applicationContext)
            return vm
        }
    }

    val dataLoading = ObservableBoolean(false)

    private val mIsDataLoadingError = ObservableBoolean(false)

    val empty = ObservableBoolean(false)

    val posts: ObservableList<Post> = ObservableArrayList<Post>()

    private val mOpenEntryEvent = SingleLiveEvent<String>()

    private val newEntryEvent = SingleLiveEvent<Void>()

    val noTasksLabel = ObservableField<String>()

    val noTaskIconRes = ObservableField<Drawable>()

    val entriesAddViewVisible = ObservableBoolean()

    @SuppressLint("StaticFieldLeak")
    private var mContext: Context = context.applicationContext // To avoid leaks, this must be an Application Context.

    private lateinit var postRepository: Repository

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
            postRepository.refreshPosts()
        }

        postRepository.getPosts(object : DataSource.LoadCallback<Post> {
            override fun onLoaded(entries: List<Post>) {
                val entriesToShow = ArrayList<Post>(entries)
                if (showLoadingUI) {
                    dataLoading.set(false)
                }
                mIsDataLoadingError.set(false)
                posts.clear()
                posts.addAll(entriesToShow)
                empty.set(posts.isEmpty())
                Log.i("TOMASZ", posts.count().toString())
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
        postRepository.refreshPosts()
    }

}
