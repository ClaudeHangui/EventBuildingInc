package com.swenson.eventbuildinginc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TaskCategory")
data class TaskCategoryItem(
    @PrimaryKey val id: Int,
    val image: String,
    val title: String
)
