package com.example.herben.tripmonitor.ui.user

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Utils

class AddUserDetailsActivity: AppCompatActivity()  {
    companion object {
        val REQUEST_CODE = 3
    }
    private val ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.addedit_activity)

        setUpToolbar()

        val addEditEntryFragment = obtainViewFragment()

        Utils.replaceFragmentInActivity(supportFragmentManager, addEditEntryFragment, R.id.contentFrame)

        subscribeToNavigationChanges()
    }

    fun onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }
    private fun subscribeToNavigationChanges() {
        val viewModel = AddUserDetailsViewModel.obtain(this)
        // The activity observes the navigation events in the ViewModel
        viewModel.userUpdatedEvent.observe(this, Observer { this@AddUserDetailsActivity.onEntrySaved() })
    }

    private fun obtainViewFragment(): AddUserDetailsFragment {
        // View Fragment
        var addEditTaskFragment: AddUserDetailsFragment? = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as AddUserDetailsFragment?

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddUserDetailsFragment()
        }
        return addEditTaskFragment
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

