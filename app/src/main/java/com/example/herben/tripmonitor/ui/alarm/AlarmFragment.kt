package com.example.herben.tripmonitor.ui.alarm

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
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
import com.example.herben.tripmonitor.databinding.FragmentAlarmsBinding
import java.util.*

class AlarmFragment : Fragment(), AlarmListener {

    companion object {
        fun newInstance() = AlarmFragment()
    }
    private var dataBinding: FragmentAlarmsBinding? = null
    private lateinit var viewModel: AlarmViewModel
    private var alarmsAdapter: AlarmAdapter = AlarmAdapter(this)
    private var alarmsList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_alarms, container, false)
        if (dataBinding == null) {
            dataBinding = FragmentAlarmsBinding.bind(root)
        }
        viewModel = AlarmViewModel.obtain(activity!!)
        dataBinding!!.viewmodel = viewModel

        setHasOptionsMenu(true)
        retainInstance = false

        return dataBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("TOMASZ", "Fragment userDetails activity created ")


        alarmsList = view!!.findViewById(R.id.items_alarms)
        val layoutManager = LinearLayoutManager(context)

        alarmsList!!.adapter = alarmsAdapter
        alarmsList!!.layoutManager = layoutManager

        viewModel.start()

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

    override fun isLeader(): Boolean {
        if(context == null)
            return false
        val repo = Injection.provideRepository(context!!)
        val leaderId = repo.mTrip.entity?.leaderId
        if(leaderId == repo.user.entity?.id && leaderId != null)
            return true
        return false
    }

    override fun add(position: Int) {
        if(activity==null)
            return
        val alarm  = viewModel.alarms[position]

        val calendar = GregorianCalendar.getInstance()
        calendar.time = alarm.date

        val openClockIntent = Intent(AlarmClock.ACTION_SET_ALARM);
        openClockIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        openClockIntent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        openClockIntent.putExtra(AlarmClock.EXTRA_MINUTES, Calendar.MINUTE);
        openClockIntent.putExtra(AlarmClock.EXTRA_DAYS, Calendar.DAY_OF_WEEK);
        activity!!.startActivity(openClockIntent);
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.setTitle(R.string.alarms)
    }
}
