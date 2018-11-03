package com.example.herben.tripmonitor.data.remote

import com.example.herben.tripmonitor.data.Post

interface PostFirebaseProvider {
    fun insertEntry(entry: Post)

    fun deleteEntry(entryId: String)

    fun getAllEntries(): List<Post>

    fun getEntryById(entryId: String): Post?
}