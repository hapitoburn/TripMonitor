package com.example.herben.tripmonitor.data.remote

import android.util.Log
import com.example.herben.tripmonitor.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class RemoteDatabase : FirebaseProvider{
    override fun getAlarms(tripId: String, callback: DataSource.LoadCallback<Alarm>) {

        databaseReference.child("Alarms").child(tripId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entries : MutableList<Alarm> = mutableListOf()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<Alarm>(Alarm::class.java)
                    entries.add(entry!!)
                }
                callback.onLoaded(entries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onDataNotAvailable()
                Log.e("DatabaseError", databaseError.details)
            }
        })
    }

    override fun insertAlarm(alarm: Alarm) {
        databaseReference.child("Alarms").child(alarm.tripId!!).child(alarm.id).setValue(alarm)
    }

    override fun getUserId(callback: DataSource.GetCallback<User>) {
        checkoutUser()
        Log.i("TOMASZ", "RemoteDatabase : Getting userId from uid=$usersUid")
        databaseReference.child("Users").child(usersUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temp = dataSnapshot.getValue<User>(User::class.java)
                getUserById(temp!!.id, callback)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TOMASZ", "getUserId failed")
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getUsersFromList(users: List<String>, callback: DataSource.LoadCallback<User>) {
        val entries : MutableList<User> = mutableListOf()

        databaseReference.child("PublicUsers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                entries.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<User>(User::class.java)
                    for(x in users) {
                        if (x == entry!!.id)
                            entries.add(entry)
                    }
                }
                callback.onLoaded(entries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseError", databaseError.details)
                callback.onDataNotAvailable()
            }
        })
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
        databaseReference.child("Trip").addValueEventListener(object : ValueEventListener {
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

    override fun getUserById(entryId: String, callback: DataSource.GetCallback<User>) {
        databaseReference.child("PublicUsers").child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userById = dataSnapshot.getValue<User>(User::class.java)
                callback.onLoaded(userById)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback.onDataNotAvailable()
                Log.e("Read Posts", "Failed")
            }
        })
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
        databaseReference.child("Trip").addValueEventListener(object : ValueEventListener {
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

    override fun getTripById(entryId: String, callback: DataSource.GetCallback<Trip>){
        checkoutUser()
        databaseReference.child("Trip").child(entryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tripEntry = dataSnapshot.getValue<Trip>(Trip::class.java)
                callback.onLoaded(tripEntry)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Read Posts", "Failed")
                callback.onDataNotAvailable()
            }
        })
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
        databaseReference.child("Posts").child(entry.tripId).child(entryId).setValue(entry)

    }

    override fun deletePost(entryId: String) {
        checkoutUser()
    }

    override fun getAllPosts(tripId: String, callback: DataSource.LoadCallback<Post>) {
        checkoutUser()
        databaseReference.child("Posts").child(tripId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postEntries : MutableList<Post> = mutableListOf()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue<Post>(Post::class.java)
                    postEntries.add(entry!!)
                }
                callback.onLoaded(postEntries)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback.onDataNotAvailable()
                Log.e("DatabaseError", databaseError.details)
            }
        })
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