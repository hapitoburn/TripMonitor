package com.example.herben.tripmonitor

import android.content.Context
import android.content.Intent
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
import com.example.herben.tripmonitor.common.Utils
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp


class AuthActivity : AppCompatActivity() {
    val TAG : String = "TOMASZ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_auth)

        FirebaseApp.initializeApp(this)

        if(FirebaseAuth.getInstance().currentUser == null) {
            singIn()
        }
        else {
            startTabbedActivity()
        }
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

            if (resultCode == RESULT_OK && FirebaseAuth.getInstance().currentUser != null) {
                startTabbedActivity()
            } else {
                Toast.makeText(this,
                        "Sign in failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun startTabbedActivity() {
        val MY_PREFS_NAME = "UserUid"
        val userUid = FirebaseAuth.getInstance().currentUser?.uid

        val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
        editor.putString("UserUid", userUid)
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
        fun getContextOfApplication(): Context {
            return instance.applicationContext
        }
        lateinit var instance: AuthActivity private set
    }
}
