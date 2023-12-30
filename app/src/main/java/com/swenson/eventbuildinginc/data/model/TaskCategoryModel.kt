package com.swenson.eventbuildinginc.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "SelectedSubcategories", primaryKeys = [ "child_category", "parent_category" ])
data class SelectedSubcategoryItem(
    @ColumnInfo(name = "child_category") val childCategoryId: Int,
    @ColumnInfo(name = "parent_category") val parentCategoryId: Int
)
