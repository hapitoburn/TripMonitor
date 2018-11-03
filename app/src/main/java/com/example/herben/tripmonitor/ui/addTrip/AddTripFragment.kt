package com.example.herben.tripmonitor.ui.addTrip

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R

class AddTripFragment : Fragment() {

    companion object {
        fun newInstance() = AddTripFragment()
    }

    private lateinit var viewModel: AddTripViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.add_trip_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddTripViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
