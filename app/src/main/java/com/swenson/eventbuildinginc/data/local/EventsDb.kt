package com.swenson.eventbuildinginc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailRemote
import com.swenson.eventbuildinginc.data.model.TaskCategoryLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryRemote

@Database(
    entities = [
        TaskCategoryRemote::class,
        TaskCategoryLocal::class,
        TaskCategoryDetailRemote::class,
        TaskCategoryDetailLocal::class,
    ],
    version = 1
)
abstract class EventsDb : RoomDatabase() {
    abstract fun eventsDao(): EventDao
}