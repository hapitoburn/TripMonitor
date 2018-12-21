package com.example.herben.tripmonitor.ui.trip

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.herben.tripmonitor.databinding.FragmentTripBinding

import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripFragment

class TripOverwiewFragment () : Fragment() {

    companion object {
        fun newInstance() : TripOverwiewFragment{
            return TripOverwiewFragment()
        }
    }

    private lateinit var viewModel: TripOverwiewViewModel

    private var postsBinding: FragmentTripBinding? = null

    private var tripId: String = String()

    private var adapter: TripOverviewAdapter = TripOverviewAdapter()

    private var entriesList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        postsBinding = FragmentTripBinding.inflate(inflater, container, false)

        viewModel = TripOverwiewViewModel.obtain(activity!!)

        postsBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)

        return postsBinding?.root
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entriesList = view.findViewById(R.id.item_trip_places)

        val layoutManager = LinearLayoutManager(context)

        entriesList!!.layoutManager = layoutManager

        //entriesList.addItemDecoration(SimpleDividerItemDecoration(context))

        entriesList!!.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    //override fun onListItemClick(post: Post) {
    //    board?.getOpenEntryEvent()?.value = post.id?
    //}


    private fun setupFab() {
        val fab = activity!!.findViewById(R.id.fab_action_add) as FloatingActionButton
        //fab.setOnClickListener { viewModel?.addNewEntry() }
    }


    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_logout -> signout()
        }
        return true
    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //setupSnackbar()

        setupFab()

        //setupRefreshLayout()
    }

}
