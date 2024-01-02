package com.swenson.eventbuildinginc.ui.presentation

import com.swenson.eventbuildinginc.data.model.ParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailUiModel
import com.swenson.eventbuildinginc.data.model.UpdateParentCategoryDetailUiModel
import com.swenson.eventbuildinginc.ui.base.UiEvent
import com.swenson.eventbuildinginc.ui.base.UiState
import javax.annotation.concurrent.Immutable

@Immutable
sealed class CategoryDetailScreenUiEvent: UiEvent {
    data class ShowData(val dataSet: ParentCategoryDetailUiModel): CategoryDetailScreenUiEvent()
    data class OnChangeErrorVisibility(val showError: Boolean): CategoryDetailScreenUiEvent()
    object Retry: CategoryDetailScreenUiEvent()
    data class OnItemChangeIconState(val index: Int, val isSelectedCategorySaved: UpdateParentCategoryDetailUiModel): CategoryDetailScreenUiEvent()
}

@Immutable
data class CategoryDetailScreenState(
    val isLoading: Boolean,
    val data: List<TaskCategoryDetailUiModel>,
    val showError: Boolean,
    val overallMinBudget: String,
    val overAllMaxBudget: String
    ): UiState {
        companion object {
            fun initial() = CategoryDetailScreenState(
                isLoading = true,
                data = emptyList(),
                showError = false,
                overallMinBudget = "",
                overAllMaxBudget = ""
            )
        }
    }