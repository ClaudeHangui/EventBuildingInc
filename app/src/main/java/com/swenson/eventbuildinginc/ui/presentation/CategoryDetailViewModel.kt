package com.swenson.eventbuildinginc.ui.presentation

import androidx.lifecycle.viewModelScope
import com.swenson.eventbuildinginc.data.EventRepository
import com.swenson.eventbuildinginc.ui.base.BaseReducer
import com.swenson.eventbuildinginc.ui.base.BaseViewModel
import com.swenson.eventbuildinginc.util.IoDispatcher
import com.swenson.eventbuildinginc.util.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        repository.getAllItemsForTask(catId).catch {
            println("repository error: $it")
            it.printStackTrace()
            sendEvent(CategoryDetailScreenUiEvent.OnChangeErrorVisibility(true))
        }.collectLatest { list ->
            println("dataset size: ${list.subcategories}")
            sendEvent(CategoryDetailScreenUiEvent.ShowData(list))
        }
    }

    fun onItemIconChanged(index: Int, catId: Int, parentId: Int, addItemToList: Boolean) = viewModelScope.launch(ioDispatcher) {
        repository.updateItemSelectedStatus(catId, parentId, addItemToList).collectLatest {
            sendEvent(CategoryDetailScreenUiEvent.OnItemChangeIconState(index, it))
        }
    }


    private fun sendEvent(event: CategoryDetailScreenUiEvent) {
        reducer.sendEvent(event)
    }
    fun changeErrorVisibilityState(show: Boolean){
        sendEvent(CategoryDetailScreenUiEvent.OnChangeErrorVisibility(show))
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
                        showError = false,
                        overallMinBudget = event.dataSet.budgetRange.overallMinBudget,
                        overAllMaxBudget = event.dataSet.budgetRange.overallMaxBudget
                        ))
                }

                is CategoryDetailScreenUiEvent.OnChangeErrorVisibility -> {
                    println("event status: $event")
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
                        isItemSelected = event.isSelectedCategorySaved.saveCategoryEvent
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