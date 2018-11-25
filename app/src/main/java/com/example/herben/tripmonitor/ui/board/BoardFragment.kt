package com.example.herben.tripmonitor.ui.board

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.databinding.FragmentPostsBinding;

class BoardFragment : Fragment()/*, BoardAdapter.ListItemClickListener*/ {

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
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_tabbed, menu)
    }
    //override fun onListItemClick(post: Post) {
    //    board?.getOpenEntryEvent()?.value = post.id?
    //}


    private fun setupFab() {
        val fab = activity!!.findViewById(R.id.fab_action_add) as FloatingActionButton
        fab.setOnClickListener { board?.addNewEntry() }
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