package com.swenson.eventbuildinginc.data

import com.swenson.eventbuildinginc.data.local.EventDao
import com.swenson.eventbuildinginc.data.model.OverallBudgetRange
import com.swenson.eventbuildinginc.data.model.ParentCategoryBudgetRange
import com.swenson.eventbuildinginc.data.model.ParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.model.ParentCategoryUiModel
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailLocal
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.model.TaskCategoryLocal
import com.swenson.eventbuildinginc.data.model.UpdateParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.remote.EventsApi
import com.swenson.eventbuildinginc.domain.FormatAmountUseCase
import com.swenson.eventbuildinginc.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val eventsApi: EventsApi,
    private val formatAmountUseCase: FormatAmountUseCase,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) {

    fun fetchAllEvents() = eventDao.getAllTasks().map {
        val average = getOverallAverageBudgetForSelectedItems()
        ParentCategoryUiModel(it, average)
    }.onEach {
        if (it.list.isEmpty()) {
            refreshEventList()
        }
    }


    private suspend fun refreshEventList() {
        eventsApi.getTaskCategories().also { categories ->
            categories?.let {
                eventDao.insertAllTasks(categories)
            }
        }
    }


    fun getAllItemsForTask(parentTask: Int) = eventDao.getAllSubTasks(parentTask).map {
        val budgetRange = getBudgetRange(parentTask)
        val uiModel = it.map { item ->
            TaskCategoryDetailUiModel(
                item.id, item.image, item.title, item.minBudget, item.maxBudget, item.avgBudget, item.parentCategory
            )
        }
        uiModel.forEach { item ->
            item.isItemSelected = isItemAlreadySaved(item.id)
        }

        ParentCategoryDetailUiModel(uiModel, budgetRange)
    }.onEach {
        if (it.subcategories.isEmpty()) {
            refreshSubtaskList(parentTask)
        }
    }

    private suspend fun refreshSubtaskList(parentTask: Int) {
        eventsApi.getTaskCategoriesDetail(parentTask).also { subCategories ->
            subCategories?.let {
                it.map { item -> item.parentCategory = parentTask }
                eventDao.insertAllTasksDetails(subCategories)
            }
        }
    }

    private suspend fun getBudgetRange(parentCategoryId: Int): ParentCategoryBudgetRange {
        val budget = eventDao.getCurrentEstimatedBudgetForCategory(parentCategoryId)
        val range = if (budget.isEmpty()) {
            ParentCategoryBudgetRange("", "")
        } else {
            val minBudget = budget.sumOf {
                it.minBudget ?: 0
            }

            val maxBudget = budget.sumOf {
                it.maxBudget ?: 0
            }
            ParentCategoryBudgetRange(
                formatAmountUseCase.formatInt(minBudget),
                formatAmountUseCase.formatInt(maxBudget)
            )
        }
        return range
    }

    fun updateItemSelectedStatus(categoryId: Int, parentId: Int, addItemToList: Boolean) = flow {
        val result = if (addItemToList) {
            addItemToSavedList(parentId, categoryId)
        } else {
            val count = eventDao.decrementSelectedCount(parentId)
            val isUnsaved = eventDao.markItemAsUnSaved(categoryId, parentId)
            count <= 0 && isUnsaved <= 0
        }
        println("updateItemSelectedStatus: $result")

        val budgetRange = getBudgetRange(parentId)
        emit(UpdateParentCategoryDetailUiModel(result, budgetRange))
    }.flowOn(ioDispatcher)

    private suspend fun addItemToSavedList(parentId: Int, childId: Int): Boolean {
        val childCatExists = isItemAlreadySaved(childId)
        return if (childCatExists){
            false
        } else {
            val newChildCategory = TaskCategoryDetailLocal(childId, parentId)
            val isItemSaved = eventDao.insertChildCategory(newChildCategory).toInt()

            val parentCatExists = eventDao.checkIfCategoryAlreadyExists(parentId)
            val isCountUpdated = if (parentCatExists == 1) {
                eventDao.updateSelectedCount(parentId)
            } else {
                val newTask = TaskCategoryLocal(parentId, 1)
                eventDao.insertSelectedCount(newTask).toInt()
            }
            (isItemSaved > 0) && (isCountUpdated > 0)
        }
    }

    private suspend fun getOverallAverageBudgetForSelectedItems(): String {
        val average = eventDao.getOverallAverageBudget()
        println("average: $average")
        return if (average.isEmpty()) {
            ""
        } else {
            formatAmountUseCase.formatDouble(average.average())
        }
    }

    suspend fun getOverallBudgetEstimateForSelectedItems(): OverallBudgetRange {
        val budgetEstimate = eventDao.getOverallEstimatedBudgetRange()
        return if (budgetEstimate.isEmpty()){
            OverallBudgetRange("","")
        } else {
            val minBudget = budgetEstimate.sumOf {
                it.minBudget ?: 0
            }

            val maxBudget = budgetEstimate.sumOf {
                it.maxBudget ?: 0
            }

            OverallBudgetRange(
                formatAmountUseCase.formatInt(minBudget),
                formatAmountUseCase.formatInt(maxBudget)
            )
        }
    }

    fun hasUserSavedAtLeastOneEvent() = eventDao.hasUserSavedAtLeastOneItem()

    private suspend fun isItemAlreadySaved(id: Int): Boolean {
        return eventDao.checkIfItemHasBeenSaved(id) == 1
    }
}
