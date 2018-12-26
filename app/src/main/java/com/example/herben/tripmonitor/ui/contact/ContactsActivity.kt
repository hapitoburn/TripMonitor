package com.example.herben.tripmonitor.ui.contact

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.data.TripPlaceInfo
import com.google.android.gms.location.places.ui.PlaceAutocomplete


class ContactsActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 2
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
    private val ADD_EDIT_RESULT_OK: Int = Activity.RESULT_FIRST_USER + 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_activity)
        setUpToolbar()
        Utils.replaceFragmentInActivity(supportFragmentManager, obtainViewFragment(), R.id.contentFrame)
    }

    private fun onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    private fun obtainViewFragment(): ContactsFragment {
        // View Fragment
        var addEditTaskFragment: ContactsFragment? = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as ContactsFragment?

        if (addEditTaskFragment == null) {
            addEditTaskFragment = ContactsFragment()
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
