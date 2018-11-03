package com.example.herben.tripmonitor

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.herben.tripmonitor.ui.board.TabbedActivity

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

import java.util.Arrays
import android.util.Log
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main)
        if(FirebaseAuth.getInstance().currentUser == null)
            singIn()
        //else
        startTabbedActivity()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                Log.i("TOMASZ","Sign in")
                startTabbedActivity()
            } else {
                Log.i("TOMASZ","Sign in failed")
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
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
                //AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)

    }

    companion object {
        private const val RC_SIGN_IN = 100
        fun getContextOfApplication(): Context {
            return instance.applicationContext
        }

        lateinit var instance: MainActivity private set
    }


    /*
    private void singOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }*/
}
