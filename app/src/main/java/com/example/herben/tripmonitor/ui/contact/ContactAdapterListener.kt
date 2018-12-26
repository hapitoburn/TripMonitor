package com.example.herben.tripmonitor.ui.contact

interface ContactAdapterListener {
    fun remove(position : Int)
    fun isLeader() : Boolean
}