package com.example.herben.tripmonitor.ui.alarm

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.Utils
import java.util.*

class AlarmActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.addedit_activity)

        val fragment = obtainViewFragment()

        setUpToolbar()

        Utils.replaceFragmentInActivity(supportFragmentManager, fragment, R.id.contentFrame)

        subscribeToNavigationChanges()
    }

    private fun subscribeToNavigationChanges() {
        //val viewModel = AlarmViewModel.obtain(this)
        // The activity observes the navigation events in the ViewModel
        //viewModel.userUpdatedEvent.observe(this, Observer { this@AlarmActivity.onEntrySaved() })
    }

    private fun obtainViewFragment(): AlarmFragment {
        // View Fragment
        var addEditTaskFragment: AlarmFragment? = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as AlarmFragment?

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AlarmFragment()
        }
        return addEditTaskFragment
    }

    private fun setUpToolbar() {
        // Set up the toolbar.
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
    }
}

