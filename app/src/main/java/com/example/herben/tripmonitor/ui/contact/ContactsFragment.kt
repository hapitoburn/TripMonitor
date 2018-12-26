package com.example.herben.tripmonitor.ui.contact

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.SimpleDividerItemDecoration
import com.example.herben.tripmonitor.common.SnackbarMessage
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.databinding.FragmentContactsBinding

class ContactsFragment : Fragment(), ContactAdapterListener {
    override fun isLeader(): Boolean {
        val repo = Injection.provideRepository(context!!)
        if(repo.user.entity?.id == repo.mTrip.entity?.leaderId && repo.user.entity != null)
            return true
        return false
    }

    override fun remove(position: Int) {
        viewModel.users.removeAt(position)
    }

    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel
    private var dataBinding : FragmentContactsBinding? = null

    private var contactsAdapter: ContactAdapter = ContactAdapter(this)

    private var contactsList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        dataBinding = FragmentContactsBinding.inflate(inflater, container, false)

        viewModel = ContactsViewModel.obtain(activity!!)
        viewModel.start()

        dataBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsList = view.findViewById(R.id.items_contacts)
        val usersLayoutManager = LinearLayoutManager(context)
        contactsList!!.layoutManager = usersLayoutManager
        contactsList!!.addItemDecoration(SimpleDividerItemDecoration(context!!))

        contactsList!!.adapter = contactsAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()

        setupFab()

        setupSnackbar()

        setupActionBar()
    }

    private fun loadData() {

    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(@StringRes snackbarMessageResourceId: Int) {
                Utils.showSnackbar(view, getString(snackbarMessageResourceId))
            }
        })
    }

    private fun setupFab() {

    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.setTitle(R.string.contacts)
    }

}
