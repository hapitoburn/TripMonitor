package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.herben.tripmonitor.data.Trip

@Dao
interface TripDao {
    @Query("SELECT * from trips")
    fun getTrips(): List<Trip>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: String): Trip?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trip: Trip)

    @Query("DELETE from trips")
    fun deleteAll()

    @Query("DELETE FROM trips WHERE id = :tripId")
    fun deleteTripById(tripId: String)
}