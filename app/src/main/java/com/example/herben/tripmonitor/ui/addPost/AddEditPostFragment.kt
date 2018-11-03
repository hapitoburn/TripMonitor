package com.example.herben.tripmonitor.ui.addPost

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
import com.example.herben.tripmonitor.databinding.AddeditPostFragmentBinding

class AddEditPostFragment : Fragment() {
    private var viewModel : AddEditPostViewModel? = null
    private var dataBinding : AddeditPostFragmentBinding? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()

        setupFab()

        setupSnackbar()

        setupActionBar()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.addedit_post_fragment, container, false)

        if (dataBinding == null) {
            dataBinding = AddeditPostFragmentBinding.bind(root)
        }

        viewModel = AddEditPostViewModel.obtain(activity!!)

        dataBinding!!.setViewmodel(viewModel)

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.getRoot()
    }
    private fun loadData() {
        // Add or edit an existing task?
        if (arguments != null) {
            viewModel!!.start(arguments!!.getString(ARGUMENT_EDIT_ENTRY_ID))
        } else {
            viewModel!!.start(null)
        }
    }

    private fun setupSnackbar() {
        viewModel!!.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(@StringRes snackbarMessageResourceId: Int) {
                Utils.showSnackbar(view, getString(snackbarMessageResourceId))
            }
        })
    }

    private fun setupFab() {
        val fab : FloatingActionButton? = activity?.findViewById(R.id.fab_edit_task_done)
        //fab.setImageResource(R.drawable.ic_done)
        fab?.setOnClickListener { viewModel!!.saveEntry() }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        if (arguments != null && arguments!!.get(ARGUMENT_EDIT_ENTRY_ID) != null) {
            actionBar.setTitle(R.string.edit_post)

        } else {
            actionBar.setTitle(R.string.add_post)
        }
    }
    
    companion object {
        val ARGUMENT_EDIT_ENTRY_ID = "EDIT_ENTRY_ID"
    }
}