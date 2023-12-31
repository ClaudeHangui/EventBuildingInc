package com.swenson.eventbuildinginc.ui.presentation

import androidx.lifecycle.viewModelScope
import com.swenson.eventbuildinginc.data.EventRepository
import com.swenson.eventbuildinginc.domain.Resource
import com.swenson.eventbuildinginc.ui.base.BaseReducer
import com.swenson.eventbuildinginc.ui.base.BaseViewModel
import com.swenson.eventbuildinginc.util.IoDispatcher
import com.swenson.eventbuildinginc.util.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val repository: EventRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<CategoryDetailScreenState, CategoryDetailScreenUiEvent>() {

    private val reducer = CategoryDetailReducer(CategoryDetailScreenState.initial())

    override val state: StateFlow<CategoryDetailScreenState>
        get() = reducer.state


    fun getAllSubcategories(catId: Int) = viewModelScope.launch(ioDispatcher) {
        val result = when (val categoriesResult = repository.fetchAllSubCategories(catId)) {
            is Resource.Success -> {
                CategoryDetailScreenUiEvent.ShowData(categoriesResult.data!!)
            }

            is Resource.Error -> {
                if (categoriesResult.data?.subcategories.isNullOrEmpty()) {
                    CategoryDetailScreenUiEvent.OnChangeErrorVisibility(true)
                } else {
                    CategoryDetailScreenUiEvent.ShowData(categoriesResult.data!!)
                }
            }
        }
        withContext(mainDispatcher) {
            sendEvent(result)
        }
    }

    fun onItemIconChanged(index: Int, catId: Int, parentId: Int) = viewModelScope.launch {
        repository.setCategoryStatus(catId, parentId).collectLatest {
            sendEvent(CategoryDetailScreenUiEvent.OnItemChangeIconState(index, it))
        }
    }


    private fun sendEvent(event: CategoryDetailScreenUiEvent) {
        reducer.sendEvent(event)
    }

    private class CategoryDetailReducer(init: CategoryDetailScreenState) :
        BaseReducer<CategoryDetailScreenState, CategoryDetailScreenUiEvent>(init) {
        override fun reduce(
            oldState: CategoryDetailScreenState,
            event: CategoryDetailScreenUiEvent
        ) {
            when (event) {
                is CategoryDetailScreenUiEvent.ShowData -> {
                    setState(oldState.copy(
                        isLoading = false,
                        data = event.dataSet.subcategories,
                        overallMinBudget = event.dataSet.budgetRange.overallMinBudget,
                        overAllMaxBudget = event.dataSet.budgetRange.overallMaxBudget
                        ))
                }

                is CategoryDetailScreenUiEvent.OnChangeErrorVisibility -> {
                    setState(
                        oldState.copy(
                            isLoading = !event.showError,
                            showError = event.showError
                        )
                    )
                }

                is CategoryDetailScreenUiEvent.Retry -> {
                    setState(oldState.copy(isLoading = true, showError = false))
                }

                is CategoryDetailScreenUiEvent.OnItemChangeIconState -> {
                    val newList = oldState.data.toMutableList()
                    newList[event.index] = newList[event.index].copy(
                        isCategorySaved = event.isSelectedCategorySaved.saveCategoryEvent
                    )
                    setState(oldState.copy(
                        data = newList,
                        overallMinBudget = event.isSelectedCategorySaved.budgetRange.overallMinBudget,
                        overAllMaxBudget = event.isSelectedCategorySaved.budgetRange.overallMaxBudget
                        ))
                }
            }
        }
    }
}