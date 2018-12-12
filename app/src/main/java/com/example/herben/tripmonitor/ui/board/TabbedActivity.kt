package com.example.herben.tripmonitor.ui.board

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.annotation.NonNull
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.AuthActivity
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.ui.addPost.AddEditPostActivity
import com.example.herben.tripmonitor.ui.addTrip.AddEditTripActivity
import com.example.herben.tripmonitor.ui.searchTrip.SearchTripFragment
import com.example.herben.tripmonitor.ui.trip.TripOverwiewFragment
import com.example.herben.tripmonitor.ui.user.AddUserDetailsFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import kotlinx.android.synthetic.main.activity_tabbed.*
import kotlinx.android.synthetic.main.fragment_tabbed.view.*

class TabbedActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var prefs: SharedPreferences? = null
    private val TRIP_ID = "TripId"
    private lateinit var mAuthListener : FirebaseAuth.AuthStateListener
    private var isUserNew = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO check if above is safe
        setContentView(R.layout.activity_tabbed)

        isUserNew = intent.getBooleanExtra(AuthActivity.NEW_USER, false)
        if(isUserNew)
        {
            val activity = Intent(this.applicationContext, AddUserDetailsFragment::class.java)
            startActivity(activity)
        }

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter


        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab_action_add.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        setUpSharedPreferences()

        val mViewModel = BoardViewModel.obtain(this)

        mViewModel.getNewEntryEvent().observe(this, Observer { this@TabbedActivity.addNewPost() })


    }
    private fun addNewPost() {
        val intent = Intent(this, AddEditPostActivity::class.java)
        Log.i("TOMASZ", "dodaj nowy post")
        startActivityForResult(intent, AddEditPostActivity.REQUEST_CODE)
    }

    private fun addNewTrip() {
        val intent = Intent(this, AddEditTripActivity::class.java)
        startActivityForResult(intent, AddEditTripActivity.REQUEST_CODE)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_tabbed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        if (id == R.id.logout) {
            logout()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        Log.i("TOMASZ", "trying to log out")
       // AuthActivity.singOut(this)
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Log.i("TOMASZ", "completed")
                    finish()
                }
    }

    private fun setUpSharedPreferences() {
        prefs = applicationContext.getSharedPreferences(AuthActivity.USER_UID, Context.MODE_PRIVATE)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when(position){
                1 -> BoardFragment.newInstance()
                2 -> {
                    val id : String? = prefs!!.getString(TRIP_ID, "")
                    if(id.isNullOrEmpty()) {
                        SearchTripFragment.newInstance()
                    }
                    else {
                        TripOverwiewFragment.newInstance(id!!)
                    }
                }
                else -> PlaceholderFragment.newInstance(position + 1)
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_tabbed, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
