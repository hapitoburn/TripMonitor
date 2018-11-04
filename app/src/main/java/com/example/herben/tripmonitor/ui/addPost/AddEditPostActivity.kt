package com.example.herben.tripmonitor.ui.addPost

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Utils

class AddEditPostActivity : AppCompatActivity() {
    companion object {
        val REQUEST_CODE = 1
    }
    private val ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.addedit_activity)

        setUpToolbar()

        val addEditEntryFragment = obtainViewFragment()

        Utils.replaceFragmentInActivity(supportFragmentManager, addEditEntryFragment, R.id.contentFrame)

        subscribeToNavigationChanges()
    }

    fun onEntrySaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    private fun subscribeToNavigationChanges() {
        val viewModel = AddEditViewModel.obtain(this)
        // The activity observes the navigation events in the ViewModel
        viewModel.postUpdatedEvent.observe(this, Observer { this@AddEditPostActivity.onEntrySaved() })
    }

    private fun obtainViewFragment(): AddEditPostFragment {
        // View Fragment
        var addEditTaskFragment: AddEditPostFragment? = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as AddEditPostFragment?

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditPostFragment()

            // Send the task ID to the fragment
            val bundle = Bundle()
            bundle.putString(AddEditPostFragment.ARGUMENT_EDIT_ENTRY_ID,
                    intent.getStringExtra(AddEditPostFragment.ARGUMENT_EDIT_ENTRY_ID))
            addEditTaskFragment.setArguments(bundle)
        }
        return addEditTaskFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpToolbar() {
        // Set up the toolbar.
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
    }

}
