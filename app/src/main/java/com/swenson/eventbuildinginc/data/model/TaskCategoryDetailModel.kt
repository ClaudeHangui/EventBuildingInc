package com.swenson.eventbuildinginc.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Index
import androidx.room.PrimaryKey


abstract class TaskCategoryDetail {
    abstract val id: Int
    abstract val image: String
    abstract val title: String
    abstract val minBudget: Int
    abstract val maxBudget: Int
    abstract val avgBudget: Int
    abstract var parentCategory: Int
}

@Entity(
    tableName = "TaskCategoryDetailRemote",
    foreignKeys = [ForeignKey(
        entity = TaskCategoryRemote::class,
        childColumns = ["parent_category"],
        parentColumns = ["id"],
        onDelete = NO_ACTION
    )],
    indices = [
        Index("parent_category"),
    ]
)
data class TaskCategoryDetailRemote(
    @PrimaryKey override val id: Int,
    override val image: String,
    override val title: String,
    @ColumnInfo(name = "min_budget") override val minBudget: Int,
    @ColumnInfo(name = "max_budget") override val maxBudget: Int,
    @ColumnInfo(name = "average_budget") override val avgBudget: Int,
    @ColumnInfo(name = "parent_category") override var parentCategory: Int
) : TaskCategoryDetail()


@Entity(
    tableName = "TaskCategoryDetailLocal",
    primaryKeys = ["id", "parent_category"]
)
data class TaskCategoryDetailLocal(
    val id: Int,
    @ColumnInfo(name = "parent_category") var parentCategory: Int,
)


data class TaskCategoryDetailUiModel(
    override val id: Int,
    override val image: String,
    override val title: String,
    override val minBudget: Int,
    override val maxBudget: Int,
    override val avgBudget: Int,
    override var parentCategory: Int,
    var isItemSelected: Boolean = false
) : TaskCategoryDetail()

abstract class BudgetRange {
    abstract val minBudget: Int?
    abstract val maxBudget: Int?
}

data class CategoryBudgetRange(
    @ColumnInfo(name = "min_budget") override val minBudget: Int?,
    @ColumnInfo(name = "max_budget") override val maxBudget: Int?
) : BudgetRange()


data class OverallBudgetRange(
    val minBudget: String,
    val maxBudget: String
)