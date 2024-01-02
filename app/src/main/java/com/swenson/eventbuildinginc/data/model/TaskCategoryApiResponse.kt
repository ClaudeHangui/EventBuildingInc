package com.swenson.eventbuildinginc.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TaskCategoryRemote")
data class TaskCategoryRemote(
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

@Entity(tableName = "TaskCategoryLocal")
data class TaskCategoryLocal(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "sub_categories_selected_count", defaultValue = "0")
    val subcategoriesSelectedCount: Int
)

data class TaskCategoryUiModel(
    @ColumnInfo(name = "id")
    override val id: Int,
    @ColumnInfo(name = "image")
    override val image: String,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "sub_categories_selected_count") val subcategoriesSelectedCount: Int = 0
): TaskCategory()
