package com.example.herben.tripmonitor.ui.searchTrip

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.herben.tripmonitor.databinding.SearchTripFragmentBinding

import com.example.herben.tripmonitor.R

class SearchTripFragment : Fragment() {

    companion object {
        fun newInstance() = SearchTripFragment()
    }
    private var binding: SearchTripFragmentBinding? = null

    private var adapter: SearchTripAdapter = SearchTripAdapter()

    private var entriesList: RecyclerView? = null

    private lateinit var viewModel: SearchTripViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = SearchTripFragmentBinding.inflate(inflater, container, false)

        viewModel = SearchTripViewModel.obtain(activity!!)

        binding!!.viewmodel = viewModel

        setHasOptionsMenu(true)

        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entriesList = view.findViewById(R.id.item_search_trips)

        val layoutManager = LinearLayoutManager(context)

        entriesList!!.layoutManager = layoutManager

        //entriesList.addItemDecoration(SimpleDividerItemDecoration(context))

        entriesList!!.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_tabbed, menu)
    }
    //override fun onListItemClick(post: Post) {
    //    board?.getOpenEntryEvent()?.value = post.id?
    //}


    private fun setupFab() {
        val fab = activity!!.findViewById(R.id.fab_action_add) as FloatingActionButton
        fab.setOnClickListener { viewModel.addNewEntry() }
    }


    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_logout -> signout()
        }
        return true
    }*/

}
