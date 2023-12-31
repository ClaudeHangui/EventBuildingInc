package com.swenson.eventbuildinginc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TaskCategory")
data class TaskCategoryItem(
    @PrimaryKey
    override val id: Int,
    override val image: String,
    override val title: String
):TaskCategory()

abstract class TaskCategory {
    abstract val id: Int
    abstract val image: String
    abstract val title: String
}

data class TaskCategoryUiModel(
    override val id: Int,
    override val image: String,
    override val title: String,
    val subcategoriesSelectedCount: Int = 0
): TaskCategory()

