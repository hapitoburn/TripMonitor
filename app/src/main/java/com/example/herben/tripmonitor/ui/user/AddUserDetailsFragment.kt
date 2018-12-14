package com.example.herben.tripmonitor.ui.user

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.databinding.AddUserDetailsFragmentBinding
class AddUserDetailsFragment : Fragment() {
    companion object {
        fun newInstance() = AddUserDetailsFragment()
    }
    private var dataBinding: AddUserDetailsFragmentBinding? = null
    private lateinit var viewModel: AddUserDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.add_user_details_fragment, container, false)
        if (dataBinding == null) {
            dataBinding = AddUserDetailsFragmentBinding.bind(root)
        }

        viewModel = AddUserDetailsViewModel.obtain(activity!!)

        dataBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("TOMASZ", "Fragment userDetails activity created ")

        viewModel.start()
        setupFab()

        setupSnackbar()

        setupActionBar()
    }
    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(@StringRes snackbarMessageResourceId: Int) {
                Utils.showSnackbar(view, getString(snackbarMessageResourceId))
            }
        })
    }

    private fun setupFab() {
        Log.i("TOMASZ", "setting up Fab")
        val fab : FloatingActionButton? = activity?.findViewById(R.id.fab_edit_task_done)
        fab?.setOnClickListener { viewModel.saveEntry() }
    }
    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.setTitle(R.string.edit_user)
    }

}
