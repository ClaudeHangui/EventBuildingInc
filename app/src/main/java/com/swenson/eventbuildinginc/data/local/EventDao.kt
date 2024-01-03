package com.swenson.eventbuildinginc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swenson.eventbuildinginc.data.model.CategoryBudgetRange
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailRemote
import com.swenson.eventbuildinginc.data.model.TaskCategoryLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryRemote
import com.swenson.eventbuildinginc.data.model.TaskCategoryUiModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllTasks(taskCategory: List<TaskCategoryRemote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTasksDetails(taskCategoryDetail: List<TaskCategoryDetailRemote>)

    @Query("select TaskCategoryRemote.id, TaskCategoryRemote.image, TaskCategoryRemote.title, TaskCategoryLocal.sub_categories_selected_count from TaskCategoryRemote left join TaskCategoryLocal on TaskCategoryRemote.id = TaskCategoryLocal.id")
    fun getAllTasks(): Flow<List<TaskCategoryUiModel>>

    @Query("select * from TaskCategoryDetailRemote where parent_category = :catId")
    fun getAllSubTasks(catId: Int): Flow<List<TaskCategoryDetailRemote>>

    @Query("update TaskCategoryLocal set sub_categories_selected_count = sub_categories_selected_count + 1 where id = :parentId")
    suspend fun updateSelectedCount(parentId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedCount(newTask: TaskCategoryLocal): Long

    @Query("update TaskCategoryLocal set sub_categories_selected_count = sub_categories_selected_count - 1 where id = :parentId")
    suspend fun decrementSelectedCount(parentId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildCategory(item: TaskCategoryDetailLocal): Long

    @Query("delete from TaskCategoryDetailLocal where id = :categoryId and parent_category = :parentId")
    suspend fun markItemAsUnSaved(categoryId: Int, parentId: Int): Int

    @Query("select exists (select 1 from TaskCategoryLocal where id = :parentId)")
    suspend fun checkIfCategoryAlreadyExists(parentId: Int): Int

    @Query("select exists (select 1 from TaskCategoryDetailLocal where id = :childId)")
    suspend fun checkIfItemHasBeenSaved(childId: Int): Int

    @Query("select min_budget, max_budget from TaskCategoryDetailRemote inner join TaskCategoryDetailLocal on TaskCategoryDetailRemote.id = TaskCategoryDetailLocal.id where TaskCategoryDetailRemote.parent_category = :parentCat")
    suspend fun getCurrentEstimatedBudgetForCategory(parentCat: Int): List<CategoryBudgetRange>

    @Query("select average_budget from TaskCategoryDetailRemote inner join TaskCategoryDetailLocal on TaskCategoryDetailRemote.id = TaskCategoryDetailLocal.id")
    suspend fun getOverallAverageBudget(): List<Int>

    @Query("select min_budget, max_budget from TaskCategoryDetailRemote inner join TaskCategoryDetailLocal on TaskCategoryDetailRemote.id = TaskCategoryDetailLocal.id")
    suspend fun getOverallEstimatedBudgetRange(): List<CategoryBudgetRange>

    @Query("select exists (select 1 from TaskCategoryDetailLocal)")
    fun hasUserSavedAtLeastOneItem(): Flow<Int>
}