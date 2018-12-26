package com.example.herben.tripmonitor.ui.searchTrip

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.example.herben.tripmonitor.databinding.SearchTripFragmentBinding

import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripActivity

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

    //override fun onListItemClick(post: Post) {
    //    board?.getOpenEntryEvent()?.value = post.id?
    //}

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        setupFab()
    }

    private fun setupFab() {
        if(activity==null)
            return
        val fab = activity!!.findViewById(R.id.fab_action_add) as FloatingActionButton
        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener {
            val intent = Intent(activity, AddEditTripActivity::class.java)
            Log.i("TOMASZ", "add trip from search")
            activity!!.startActivity(intent)
        }
    }


    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_logout -> signout()
        }
        return true
    }*/

}
