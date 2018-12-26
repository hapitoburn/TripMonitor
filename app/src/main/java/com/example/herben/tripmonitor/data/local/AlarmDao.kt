package com.example.herben.tripmonitor.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.herben.tripmonitor.data.Alarm

@Dao
interface AlarmDao {

    @Query("SELECT * from alarms where tripId = :tripId")
    fun getAlarms(tripId : String): List<Alarm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)

    @Query("DELETE from alarms")
    fun deleteAll()

    @Query("DELETE FROM alarms WHERE tripId = :tripId")
    fun deleteAlarmsByTrip(tripId: String)
}