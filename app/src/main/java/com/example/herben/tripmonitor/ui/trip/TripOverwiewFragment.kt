package com.example.herben.tripmonitor.ui.trip

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.herben.tripmonitor.R

class TripOverwiewFragment : Fragment() {

    companion object {
        fun newInstance() = TripOverwiewFragment()
    }

    private lateinit var viewModel: TripOverwiewViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.trip_overwiew_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TripOverwiewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
