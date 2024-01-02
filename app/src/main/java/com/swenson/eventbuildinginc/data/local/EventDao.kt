package com.swenson.eventbuildinginc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swenson.eventbuildinginc.data.model.CategoryBudgetRange
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailRemote
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailUiModel
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

    @Query("select TaskCategoryDetailRemote.id, TaskCategoryDetailRemote.image, TaskCategoryDetailRemote.title, TaskCategoryDetailRemote.min_budget, TaskCategoryDetailRemote.max_budget, TaskCategoryDetailRemote.average_budget, TaskCategoryDetailRemote.parent_category, TaskCategoryDetailLocal.is_item_selected from TaskCategoryDetailRemote left join TaskCategoryDetailLocal on TaskCategoryDetailRemote.id = TaskCategoryDetailLocal.id where TaskCategoryDetailRemote.parent_category = :catId")
    fun getAllSubTasks(catId: Int): Flow<List<TaskCategoryDetailUiModel>>

    @Query("update TaskCategoryLocal set sub_categories_selected_count = sub_categories_selected_count + 1 where id = :parentId")
    suspend fun updateSelectedCount(parentId: Int) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedCount(newTask: TaskCategoryLocal): Long

    @Query("update TaskCategoryLocal set sub_categories_selected_count = sub_categories_selected_count - 1 where id = :parentId")
    suspend fun decrementSelectedCount(parentId: Int): Int

    @Query("update TaskCategoryDetailLocal set is_item_selected = 1 where id = :categoryId")
    suspend fun markItemAsSaved(categoryId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildCategory(item: TaskCategoryDetailLocal): Long

    @Query("update TaskCategoryDetailLocal set is_item_selected = 0 where id = :categoryId")
    suspend fun markItemAsUnSaved(categoryId: Int): Int

    @Query("select exists (select 1 from TaskCategoryLocal where id = :parentId)")
    suspend fun checkIfCategoryAlreadyExists(parentId: Int): Int

    @Query("select exists (select 1 from TaskCategoryDetailLocal where id = :childId)")
    suspend fun checkIfItemHasBeenSaved(childId: Int): Int

    @Query("select min_budget, max_budget from TaskCategoryDetailRemote inner join TaskCategoryDetailLocal on TaskCategoryDetailRemote.id = TaskCategoryDetailLocal.id where TaskCategoryDetailRemote.parent_category = :parentCat and is_item_selected = 1")
    suspend fun getCurrentEstimatedBudget(parentCat: Int): List<CategoryBudgetRange>
}