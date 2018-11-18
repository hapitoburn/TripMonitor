package com.example.herben.tripmonitor.ui.addTrip

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.databinding.AddeditTripFragmentBinding

class AddEditTripFragment : Fragment() {

    companion object {
        fun newInstance() = AddEditTripFragment()
        const val ARGUMENT_EDIT_TRIP_ID = "EDIT_ENTRY_ID"
    }

    private lateinit var viewModel: AddEditTripViewModel
    private var dataBinding : AddeditTripFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.addedit_trip_fragment, container, false)

        if (dataBinding == null) {
            dataBinding = AddeditTripFragmentBinding.bind(root)
        }

        viewModel = AddEditTripViewModel.obtain(activity!!)

        dataBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.getRoot()
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
        fab?.setOnClickListener { viewModel.saveEntry() }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        if (arguments != null && arguments!!.get(ARGUMENT_EDIT_TRIP_ID) != null) {
            actionBar.setTitle(R.string.edit_trip)

        } else {
            actionBar.setTitle(R.string.add_post)
        }
    }

}
