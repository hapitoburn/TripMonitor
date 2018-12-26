package com.example.herben.tripmonitor.ui.alarm

interface AlarmListener {
    fun isLeader() : Boolean
    fun add(position: Int)
}