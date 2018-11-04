package com.example.herben.tripmonitor.ui.addTrip

import android.app.Activity
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Utils

class AddEditTripActivity : AppCompatActivity() {

    private val ADD_EDIT_RESULT_OK: Int = Activity.RESULT_FIRST_USER + 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addedit_activity)
        Utils.replaceFragmentInActivity(supportFragmentManager, obtainViewFragment(), R.id.contentFrame)
        subscribeToNavigationChanges()
    }

    fun onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    private fun subscribeToNavigationChanges() {
        val viewModel = AddEditTripViewModel.obtain(this)
        // The activity observes the navigation events in the ViewModel
        viewModel.postUpdatedEvent.observe(this, Observer { this@AddEditTripActivity.onEntrySaved() })
    }
    private fun obtainViewFragment(): AddEditTripFragment {
        // View Fragment
        var addEditTaskFragment: AddEditTripFragment? = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as AddEditTripFragment?

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTripFragment()

            // Send the task ID to the fragment
            val bundle = Bundle()
            bundle.putString(AddEditTripFragment.ARGUMENT_EDIT_TRIP_ID,
                    intent.getStringExtra(AddEditTripFragment.ARGUMENT_EDIT_TRIP_ID))
            addEditTaskFragment.arguments = bundle
        }
        return addEditTaskFragment
    }

}
