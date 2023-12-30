package com.swenson.eventbuildinginc.ui.presentation

import com.swenson.eventbuildinginc.data.model.SubCategory
import com.swenson.eventbuildinginc.ui.base.UiEvent
import com.swenson.eventbuildinginc.ui.base.UiState
import javax.annotation.concurrent.Immutable

@Immutable
sealed class CategoryDetailScreenUiEvent: UiEvent {
    data class ShowData(val items: List<SubCategory>): CategoryDetailScreenUiEvent()
    data class OnChangeErrorVisibility(val showError: Boolean): CategoryDetailScreenUiEvent()
    object Retry: CategoryDetailScreenUiEvent()
    data class OnItemChangeIconState(val index: Int, val isSelectedCategorySaved: Boolean): CategoryDetailScreenUiEvent()
}

@Immutable
data class CategoryDetailScreenState(
    val isLoading: Boolean,
    val data: List<SubCategory>,
    val showError: Boolean,
    ): UiState {
        companion object {
            fun initial() = CategoryDetailScreenState(
                isLoading = true,
                data = emptyList(),
                showError = false
            )
        }
    }