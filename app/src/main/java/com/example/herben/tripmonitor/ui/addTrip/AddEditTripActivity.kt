package com.example.herben.tripmonitor.ui.addTrip

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Utils
import com.google.android.gms.location.places.ui.PlaceAutocomplete


class AddEditTripActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 2
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
    private val ADD_EDIT_RESULT_OK: Int = Activity.RESULT_FIRST_USER + 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addedit_activity)
        Utils.replaceFragmentInActivity(supportFragmentManager, obtainViewFragment(), R.id.contentFrame)
        subscribeToNavigationChanges()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val position : Int? = data?.extras?.getInt(AddEditTripFragment.POSITION)
                val place = PlaceAutocomplete.getPlace(this, data!!)
                val viewModel = AddEditTripViewModel.obtain(this)
                if(position==null)
                    viewModel.places.add(place.address.toString())
                else
                    viewModel.places.add(position, place.address.toString())

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data!!)
                // TODO: Handle the error.
                Log.i("TOMASZ", status.statusMessage)

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    private fun onUsersLoaded() {
        val viewModel = AddEditTripViewModel.obtain(this)
        val userList = ArrayList<String>()
        for(user in viewModel.usersHolder.users){
            if(!user.email.isEmpty()) {
                userList.add(user.email)
            }
            if(!user.phoneNumber.isEmpty()){
                userList.add(user.phoneNumber)
            }
        }
    }

    private fun subscribeToNavigationChanges() {
        val viewModel = AddEditTripViewModel.obtain(this)
        // The activity observes the navigation events in the ViewModel
        viewModel.tripUpdatedEvent.observe(this, Observer { this@AddEditTripActivity.onEntrySaved() })
        viewModel.usersLoadedEvent.observe(this, Observer { this@AddEditTripActivity.onUsersLoaded() })
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
