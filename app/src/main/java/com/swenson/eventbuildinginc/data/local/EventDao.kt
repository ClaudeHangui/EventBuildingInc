package com.swenson.eventbuildinginc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swenson.eventbuildinginc.data.model.EventBudgetRange
import com.swenson.eventbuildinginc.data.model.SelectedSubcategoryItem
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailItem
import com.swenson.eventbuildinginc.data.model.TaskCategoryItem

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTasks(taskCategory: List<TaskCategoryItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTasksDetails(taskCategoryDetail: List<TaskCategoryDetailItem>)

    @Query("select * from TaskCategory")
    fun getAllTasks(): List<TaskCategoryItem>

    @Query("select * from TaskCategoryDetail where parent_category = :catId")
    fun getAllSubTasks(catId: Int): List<TaskCategoryDetailItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedSubcategory(item: SelectedSubcategoryItem): Long

    @Query("delete from SelectedSubcategories where child_category = :catId and parent_category = :parentId")
    fun removeSelectedSubcategory(catId: Int, parentId: Int): Int

    @Query("select exists (select 1 from SelectedSubcategories where child_category = :catId)")
    suspend fun isSubcategoryAlreadySelected(catId: Int): Int

    @Query("select min_budget, max_budget from TaskCategoryDetail inner join SelectedSubcategories on TaskCategoryDetail.id = SelectedSubcategories.child_category where TaskCategoryDetail.parent_category = :parentCat")
    suspend fun getCurrentBudget(parentCat: Int): List<EventBudgetRange>

    @Query("select exists (select 1 from SelectedSubcategories where parent_category = :parentId)")
    suspend fun isParentCategorySelected(parentId: Int): Int

    @Query("select count(*) from SelectedSubcategories where parent_category = :parentId")
    suspend fun getSubcategoriesSelectedCount(parentId: Int): Int
}