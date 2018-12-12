package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.herben.tripmonitor.data.User

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun getUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE from users")
    fun deleteAll()

    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteTripById(userId: String)

    @Query("UPDATE users SET trip=:tripId WHERE id = :userId")
    fun updateActiveTrip(userId: String, tripId: String)

    @Query("UPDATE users SET name=:name, phoneNumber=:phoneNumber WHERE id = :userId")
    fun updateInfo(name: String, phoneNumber: String, userId : String)

}