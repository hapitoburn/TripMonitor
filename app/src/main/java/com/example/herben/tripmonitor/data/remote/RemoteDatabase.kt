package com.example.herben.tripmonitor.data.remote

import android.util.Log
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RemoteDatabase : FirebaseProvider{
    override fun updateUserInfo(name: String, phoneNumber: String, userId: String) {
        checkoutUser()
        databaseReference.child("Users").child(usersUid).child("name").setValue(name)
        databaseReference.child("Users").child(usersUid).child("phoneNumber").setValue(phoneNumber)
    }

    override fun insertUser(userId: String) {
        checkoutUser()
        databaseReference.child("Users").child(usersUid).setValue(User("", "", "", userId))
    }

    override fun updateActiveTrip(userId: String, tripId: String) {
        databaseReference.child("Users").child(userId).child("trip").setValue(tripId)
    }

    override fun getActiveTrip(userId: String): Trip? {
        var trip : Trip? = null
        databaseReference.child("Trips").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<Trip>(Trip::class.java)
                    if(entry?.leaderId == userId){
                        trip = entry
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
            }
        })
        return trip
    }

    override fun getUserById(entryId: String): User? {
        var entry : User? = null
        databaseReference.child("Users").child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                entry = dataSnapshot.getValue<User>(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Read Posts", "Failed")
            }
        })
        return entry
    }

    override fun getAllUsers(): List<User> {
        val entries : MutableList<User> = mutableListOf()
        databaseReference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                entries.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<User>(User::class.java)
                    entries.add(entry!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
            }
        })
        return entries
    }

    override fun getAllTrips(): List<Trip> {
        val entries : MutableList<Trip> = mutableListOf()
        databaseReference.child("Trips").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                entries.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<Trip>(Trip::class.java)
                    entries.add(entry!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
            }
        })
        return entries
    }

    override fun getTripById(entryId: String): Trip? {
        checkoutUser()
        var tripEntry : Trip? = null
        databaseReference.child("Trips").child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripEntry = dataSnapshot.getValue<Trip>(Trip::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Read Posts", "Failed")
            }
        })
        return tripEntry
    }

    override fun insertTrip(entry: Trip) {
        checkoutUser()

        val entryId = entry.id
        databaseReference.child("Trip").child(entryId).setValue(entry)
    }

    private val databaseReference = FirebaseDatabase.getInstance().reference
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
        val postEntries : MutableList<Post> = mutableListOf()
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
        var postEntry: Post? = null
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