package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.example.herben.tripmonitor.data.Post

@Dao
interface PostDao {

    @Query("SELECT * from posts")
    fun getPosts(): List<Post>

    @Query("SELECT * FROM posts WHERE id = :entryId")
    fun getPostById(entryId: String): Post?

    @Insert(onConflict = REPLACE)
    fun insert(post: Post)

    @Query("DELETE from posts")
    fun deleteAll()

    @Query("DELETE FROM posts WHERE id = :entryId")
    fun deletePostById(entryId: String)
}