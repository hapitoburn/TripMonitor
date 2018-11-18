package com.example.herben.tripmonitor.data.remote

import com.example.herben.tripmonitor.data.Post

interface PostFirebaseProvider {
    fun insertPost(entry: Post)

    fun deletePost(entryId: String)

    fun getAllPosts(): List<Post>

    fun getPostById(entryId: String): Post?
}