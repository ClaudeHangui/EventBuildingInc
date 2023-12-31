package com.swenson.eventbuildinginc.data

import com.swenson.eventbuildinginc.data.local.EventDao
import com.swenson.eventbuildinginc.data.model.ParentCategoryBudgetRange
import com.swenson.eventbuildinginc.data.model.ParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.model.SelectedSubcategoryItem
import com.swenson.eventbuildinginc.data.model.SubCategory
import com.swenson.eventbuildinginc.data.model.TaskCategoryItem
import com.swenson.eventbuildinginc.data.model.UpdateParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.remote.EventsApi
import com.swenson.eventbuildinginc.domain.FormatAmountUseCase
import com.swenson.eventbuildinginc.domain.Resource
import com.swenson.eventbuildinginc.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val eventsApi: EventsApi,
    private val formatAmountUseCase: FormatAmountUseCase,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchAllCategories(): Resource<List<TaskCategoryItem>> {
        return try {
            val response = eventsApi.getTaskCategories()
            response?.let {
                eventDao.insertAllTasks(it)
            }
            val result = eventDao.getAllTasks()
            Resource.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            val result = eventDao.getAllTasks()
            Resource.Error(e.message ?: "An unknown error occurred.", result)
        }
    }

    suspend fun fetchAllSubCategories(parentCategoryId: Int): Resource<ParentCategoryDetailUiModel> {
        return try {
            val apiResponse = eventsApi.getTaskCategoriesDetail(parentCategoryId)
            apiResponse?.let {
                it.map { item ->
                    item.parentCategory = parentCategoryId
                }
                eventDao.insertAllTasksDetails(it)
            }
            val localResponse = eventDao.getAllSubTasks(parentCategoryId)
            val result = localResponse.map {
                val isSaved = isCategoryAlreadySaved(it.id)
                SubCategory(
                    it.id,
                    it.avgBudget,
                    it.image,
                    it.maxBudget,
                    it.minBudget,
                    it.title,
                    isSaved
                )
            }

            val budgetRange = getBudgetRange(parentCategoryId)
            Resource.Success(ParentCategoryDetailUiModel(result, budgetRange))
        } catch (e: Exception){
            e.printStackTrace()
            val localResponse = eventDao.getAllSubTasks(parentCategoryId)
            val result = localResponse.map {
                val isSaved = isCategoryAlreadySaved(it.id)
                SubCategory(
                    it.id,
                    it.avgBudget,
                    it.image,
                    it.maxBudget,
                    it.minBudget,
                    it.title,
                    isSaved
                )
            }

            val budgetRange = getBudgetRange(parentCategoryId)
            Resource.Error(e.message ?: "An unknown error occurred.", ParentCategoryDetailUiModel(
                result, budgetRange
            ))
        }
    }

    private suspend fun getBudgetRange(parentCategoryId: Int): ParentCategoryBudgetRange {
        val budget = eventDao.getCurrentBudget(parentCategoryId)
        val range = if (budget.isEmpty()){
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

    fun setCategoryStatus(categoryId: Int, parentId: Int) = flow {
        val result = if (isCategoryAlreadySaved(categoryId)){
            eventDao.removeSelectedSubcategory(categoryId, parentId)
            false
        } else {
            saveCategory(categoryId, parentId)
        }
        val budgetRange = getBudgetRange(parentId)
        emit(UpdateParentCategoryDetailUiModel(result, budgetRange))
    }.flowOn(ioDispatcher)

    private suspend fun saveCategory(categoryId: Int, parentId: Int) : Boolean {
        val itemSaved = eventDao.insertSelectedSubcategory(SelectedSubcategoryItem(categoryId, parentId))
        return itemSaved != 0L
    }

    private suspend fun isCategoryAlreadySaved(categoryId: Int): Boolean {
        val isItemExist = eventDao.isSubcategoryAlreadySelected(categoryId)
        return isItemExist == 1
    }
}
