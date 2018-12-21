package com.example.herben.tripmonitor.data.remote

import android.util.Log
import com.example.herben.tripmonitor.data.DataSource
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class RemoteDatabase : FirebaseProvider{
    var userById : User? = null
    override fun getUserId(callback: DataSource.GetCallback<User>) {
        checkoutUser()
        Log.i("TOMASZ", "RemoteDatabase : Getting userId from uid=$usersUid")
        var entry : User? = null
        databaseReference.child("Users").child(usersUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temp = dataSnapshot.getValue<User>(User::class.java)
                entry = getUserById(temp!!.id, callback)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TOMASZ", "getUserId failed")
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getUsersFromList(users: List<String>): List<User> {
        val entries : MutableList<User> = mutableListOf()

        databaseReference.child("PublicUsers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                entries.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<User>(User::class.java)
                    for(x in entries) {
                        if (x.id == entry!!.id)
                            entries.add(entry)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
            }
        })
        return entries
    }

    override fun updateUserInfo(name: String?, phoneNumber: String?, email: String?, userId: String) {
        if(name != null)
            databaseReference.child("PublicUsers").child(userId).child("name").setValue(name)
        if(phoneNumber != null)
            databaseReference.child("PublicUsers").child(userId).child("phoneNumber").setValue(phoneNumber)
        if(email != null)
            databaseReference.child("PublicUsers").child(userId).child("email").setValue(email)
    }

    override fun insertUser(userId: String) {
        checkoutUser()
        Log.i("TOMASZ", "Firebase insert users id $userId uid $usersUid")
        databaseReference.child("Users").child(usersUid).setValue(User(userId))
        databaseReference.child("PublicUsers").child(userId).setValue(User(userId))
    }

    override fun updateActiveTrip(userId: String, tripId: String) {
        databaseReference.child("PublicUsers").child(userId).child("trip").setValue(tripId)
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

    override fun getUserById(entryId: String, callback: DataSource.GetCallback<User>): User? {
        databaseReference.child("PublicUsers").child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userById = dataSnapshot.getValue<User>(User::class.java)
                callback.onLoaded(userById)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback.onDataNotAvailable()
                Log.e("Read Posts", "Failed")
            }
        })
        return userById
    }

    override fun getAllUsers(): List<User> {
        val entries : MutableList<User> = mutableListOf()
        databaseReference.child("PublicUsers").addValueEventListener(object : ValueEventListener {
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

    override fun getTripById(entryId: String, callback: DataSource.GetCallback<Trip>): Trip? {
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
        databaseReference.child("PublicUsers").child(entry.leaderId!!).child("trip").setValue(entryId)
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