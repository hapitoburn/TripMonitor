package com.example.herben.tripmonitor.data

class Cache<T>(var entity: T? = null) {
    var isCacheDirty = true
    fun invalidate(){
        isCacheDirty=true
        entity=null
    }
}