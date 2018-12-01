package com.example.herben.tripmonitor.ui.board

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.databinding.FragmentPostsBinding;


class BoardFragment : Fragment()/*, BoardAdapter.ListItemClickListener*/ {

    companion object {
        fun newInstance() : BoardFragment {
            return BoardFragment()
        }
    }
    private var board: BoardViewModel? = null
    private var binding: FragmentPostsBinding? = null

    private var adapter: BoardAdapter = BoardAdapter()

    private var entriesList: RecyclerView? = null

    override fun onResume() {
        super.onResume()
        board?.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentPostsBinding.inflate(inflater, container, false)

        board = BoardViewModel.obtain(activity!!)

        binding!!.viewmodel = board

        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entriesList = view.findViewById(R.id.posts_list)

        val layoutManager = LinearLayoutManager(context)

        entriesList!!.layoutManager = layoutManager

        //entriesList.addItemDecoration(SimpleDividerItemDecoration(context))

        entriesList!!.adapter = adapter
        Log.i("TOMASZ", "BOARD created")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_tabbed, menu)
    }
    //override fun onListItemClick(post: Post) {
    //    board?.getOpenEntryEvent()?.value = post.id?
    //}


    private fun setupFab() {
        if(activity == null)
            return
        val fab = activity!!.findViewById(R.id.fab_action_add) as FloatingActionButton
        Log.i("TOMASZ", "Setup fab "+  board.toString())
        fab.setOnClickListener { board?.addNewEntry() }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        setupFab()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //setupSnackbar()

        setupFab()

        //setupRefreshLayout()
    }
}