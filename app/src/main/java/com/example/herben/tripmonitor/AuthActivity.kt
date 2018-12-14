package com.example.herben.tripmonitor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import com.example.herben.tripmonitor.ui.board.TabbedActivity

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

import java.util.Arrays
import android.util.Log
import android.widget.Toast
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.common.Utils
import com.example.herben.tripmonitor.data.User
import com.example.herben.tripmonitor.ui.user.AddUserDetailsActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser




class AuthActivity : AppCompatActivity() {
    val TAG : String = "TOMASZ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_auth)

        FirebaseApp.initializeApp(this)

        //if(FirebaseAuth.getInstance().currentUser == null) {
            singIn()
        /*}
        else {
            startTabbedActivity(false)
        }
        */
    }

    override fun onResume() {
        super.onResume()
        if(FirebaseAuth.getInstance().currentUser == null) {
            singIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                if(response?.isNewUser == true){
                    val postRepository = Injection.provideRepository(applicationContext)
                    postRepository.insertUser(User().id)
                    val activity = Intent(this.applicationContext, AddUserDetailsActivity::class.java)
                    startActivityForResult(activity, UPDATE_USER)
                }
                else
                    startTabbedActivity()
            } else {
                Toast.makeText(this,
                        "Sign in failed", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == UPDATE_USER){
            if(resultCode == AddUserDetailsActivity.ADD_EDIT_RESULT_OK){
                startTabbedActivity()
            }
            else {
                val activity = Intent(this.applicationContext, AddUserDetailsActivity::class.java)
                startActivityForResult(activity, UPDATE_USER)
            }
        }
    }

    private fun startTabbedActivity() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid

        val editor = getSharedPreferences(USER_UID, MODE_PRIVATE).edit()
        editor.putString(USER_UID, userUid)
        editor.apply()

        val startTabbedActivity = Intent(this.applicationContext, TabbedActivity::class.java)
        startActivity(startTabbedActivity)
    }
    private fun singIn() {
        // Choose authentication providers
        val providers = Arrays.asList<AuthUI.IdpConfig>(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN)
    }

    companion object {
        private const val RC_SIGN_IN = 100
        private const val UPDATE_USER = 101
        const val USER_UID = "UserUid"
        const val NEW_USER = "IsUserNew"
        fun getContextOfApplication(): Context {
            return instance.applicationContext
        }
        fun  getUserUid(): String? {
            return getContextOfApplication().
                    getSharedPreferences(USER_UID, MODE_PRIVATE).getString(USER_UID, "")
        }

        lateinit var instance: AuthActivity private set
    }
}
