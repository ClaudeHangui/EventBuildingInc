package com.swenson.eventbuildinginc.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "TaskCategoryDetail",
    foreignKeys = [ForeignKey(
        entity = TaskCategoryItem::class,
        childColumns = ["parent_category"],
        parentColumns = ["id"]
    )]
    )
data class TaskCategoryDetailItem(
    @ColumnInfo (name = "average_budget") val avgBudget: Int,
    @PrimaryKey val id: Int,
    val image: String,
    @ColumnInfo (name = "max_budget") val maxBudget: Int,
    @ColumnInfo (name = "min_budget") val minBudget: Int,
    val title: String,
    @ColumnInfo (name = "parent_category") var parentCategory: Int
)