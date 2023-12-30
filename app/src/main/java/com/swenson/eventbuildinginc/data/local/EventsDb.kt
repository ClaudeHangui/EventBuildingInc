package com.swenson.eventbuildinginc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swenson.eventbuildinginc.data.model.SelectedSubcategoryItem
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailItem
import com.swenson.eventbuildinginc.data.model.TaskCategoryItem

@Database(entities = [TaskCategoryItem::class, TaskCategoryDetailItem::class, SelectedSubcategoryItem::class], version = 1)
abstract class EventsDb: RoomDatabase() {
    abstract fun eventsDao(): EventDao
}