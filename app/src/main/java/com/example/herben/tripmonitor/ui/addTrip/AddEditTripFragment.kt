package com.example.herben.tripmonitor.ui.addTrip

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.SimpleDividerItemDecoration
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.databinding.AddeditTripFragmentBinding
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete

class AddEditTripFragment : Fragment(), PlaceAdapterListener {

    companion object {
        fun newInstance() = AddEditTripFragment()
        const val ARGUMENT_EDIT_TRIP_ID = "EDIT_ENTRY_ID"
        const val POSITION = "POSITION"
    }

    private lateinit var viewModel: AddEditTripViewModel
    private var dataBinding : AddeditTripFragmentBinding? = null

    private var userAdapter: UserAdapter = UserAdapter()
    private var placeAdapter: PlaceAdapter = PlaceAdapter(this)

    private var usersList: RecyclerView? = null
    private var placesList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        dataBinding = AddeditTripFragmentBinding.inflate(inflater, container, false)

        viewModel = AddEditTripViewModel.obtain(activity!!)

        dataBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersList = view.findViewById(R.id.users_list)
        placesList = view.findViewById(R.id.places_list)
        val usersLayoutManager = LinearLayoutManager(context)
        val placesLayoutManager = LinearLayoutManager(context)
        //placesLayoutManager.stackFromEnd = true
        usersList!!.layoutManager = usersLayoutManager
        placesList!!.layoutManager = placesLayoutManager
        usersList!!.addItemDecoration(SimpleDividerItemDecoration(context!!))
        placesList!!.addItemDecoration(SimpleDividerItemDecoration(context!!))

        usersList!!.adapter = userAdapter
        placesList!!.adapter = placeAdapter
        val addUserButton : AppCompatButton? = activity?.findViewById(R.id.button_add_place)
        addUserButton?.setOnClickListener {
            try {
                val typeFilter = AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build()
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter)
                        .build(activity)
                activity?.startActivityForResult(intent, AddEditTripActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
                // TODO: Handle the error.
            } catch (e: GooglePlayServicesNotAvailableException) {
                // TODO: Handle the error.
            }
        }
    }

    override fun removePlaceAtPosition(position: Int) {
        viewModel.places.removeAt(position)
    }

    override fun addPlaceAtPosition(position: Int) {
        try {
            val typeFilter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter)
                    .build(activity)
            intent.putExtra(POSITION, position)
            activity?.startActivityForResult(intent, AddEditTripActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()

        setupFab()

        setupSnackbar()

        setupActionBar()
    }

    private fun loadData() {
        // Add or edit an existing task?
        if (arguments != null) {
            viewModel.start(arguments?.getString(ARGUMENT_EDIT_TRIP_ID))
        } else {
            viewModel.start(null)
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(@StringRes snackbarMessageResourceId: Int) {
                Utils.showSnackbar(view, getString(snackbarMessageResourceId))
            }
        })
    }

    private fun setupFab() {
        val fab : FloatingActionButton? = activity?.findViewById(R.id.fab_edit_task_done)
        //fab.setImageResource(R.drawable.ic_done)
        Log.i("TOMASZ", "Add edit trip setup fab")
        fab?.setOnClickListener { viewModel.saveEntry() }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        if (arguments != null && arguments!!.get(ARGUMENT_EDIT_TRIP_ID) != null) {
            actionBar.setTitle(R.string.edit_trip)

        } else {
            actionBar.setTitle(R.string.add_trip)
        }
    }

}
