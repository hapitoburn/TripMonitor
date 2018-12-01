package com.example.herben.tripmonitor.data.remote

import android.util.Log
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RemoteDatabase : FirebaseProvider{
    override fun getAllTrips(): List<Trip> {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return ArrayList<Trip>()
    }

    override fun getTripById(entryId: String): Trip? {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Trip()
    }

    override fun insertTrip(entry: Trip) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var postEntry: Post? = null
    private var postEntries: MutableList<Post> = mutableListOf()
    private var usersUid: String = ""

    init {

    }

    private fun checkoutUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            usersUid = user.uid
        } else {
            usersUid = ""
        }
    }

    override fun insertPost(entry: Post) {
        checkoutUser()

        val entryId = entry.id
        databaseReference.child("Posts").child(usersUid).child(entryId).setValue(entry)

    }

    override fun deletePost(entryId: String) {
        checkoutUser()

    }

    override fun getAllPosts(): List<Post> {
        checkoutUser()
        postEntries = mutableListOf()
        databaseReference.child("Posts").child(usersUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postEntries.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<Post>(Post::class.java)
                    postEntries.add(entry!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
            }
        })
        return postEntries
    }

    override fun getPostById(entryId: String): Post? {
        checkoutUser()

        databaseReference.child("Posts").child(usersUid).child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postEntry = dataSnapshot.getValue<Post>(Post::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Read Posts", "Failed")
            }
        })
        return postEntry
    }
    companion object {

        private var INSTANCE: RemoteDatabase? = null

        fun getInstance(): RemoteDatabase {
            if (INSTANCE == null) {
                INSTANCE = RemoteDatabase()
            }
            return INSTANCE!!
        }
    }
}