package com.example.herben.tripmonitor.data.local

import com.example.herben.tripmonitor.common.AppExecutors
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripViewModel


class LocalDataSource// Prevent direct instantiation.
private constructor(private val appExecutors: AppExecutors,
                    private val posts: PostDao,
                    private val users: UserDao,
                    private val trips: TripDao) : DataSource {
    override fun saveTrip(trip: Trip) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTrip(entryId: String, addEditTripViewModel: AddEditTripViewModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPosts(callback: DataSource.LoadCallback<Post>) {
        val runnable = Runnable {
            val entryList = posts.getPosts()
            appExecutors.mainThread().execute {
                if (entryList.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onLoaded(entryList)
                }
            }
        }
        appExecutors.diskIO().execute(runnable)
    }


    override fun getPost(entryId: String, callback: DataSource.GetCallback<Post>) {
        val runnable = Runnable {
            val journalEntry = posts.getPostById(entryId)

            appExecutors.mainThread().execute {
                if (journalEntry != null) {
                    callback.onLoaded(journalEntry)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.diskIO().execute(runnable)
    }

    override fun savePost(entry: Post) {
        checkNotNull(entry)
        val runnable = Runnable { posts.insert(entry) }
        appExecutors.diskIO().execute(runnable)
    }

    override fun deletePost(entryId: String) {
        val deleteRunnable = Runnable { posts.deletePostById(entryId) }
        appExecutors.diskIO().execute(deleteRunnable)
    }

    override fun refreshPosts() {
        // Not required because the {@link EntryRepository} handles the logic of refreshing the
        // entries from all the available data sources.
        // todo:setup repository to get from local or remote depending on the needs

    }

    override fun deleteAllPosts() {
        //todo:add delete all entries logic here
    }

    companion object {

        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, database: PostDatabase): LocalDataSource {
            if (INSTANCE == null) {
                synchronized(LocalDataSource::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = LocalDataSource(appExecutors, database.posts(), database.users(), database.trips())
                    }
                }
            }
            return INSTANCE!!
        }
    }
}