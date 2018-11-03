package com.example.herben.tripmonitor.ui.addTrip

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.herben.tripmonitor.R

class AddTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_trip_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AddTripFragment.newInstance())
                    .commitNow()
        }
    }

}
