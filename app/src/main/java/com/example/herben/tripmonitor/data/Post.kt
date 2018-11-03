package com.example.herben.tripmonitor.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.util.*


@Entity(tableName = "posts")
data class Post(@PrimaryKey@NonNull var id: String,
                @ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "body") var body: String,
                @ColumnInfo(name = "date") var date: Date
){
    constructor():this(UUID.randomUUID().toString(), "", "", Date());
    constructor(title : String?, body : String?, date : Date, id : String = UUID.randomUUID().toString()):this(id, title.orEmpty(), body.orEmpty(), date);
}