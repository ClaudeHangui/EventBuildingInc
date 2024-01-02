package com.swenson.eventbuildinginc.ui.presentation

import com.swenson.eventbuildinginc.data.model.TaskCategoryUiModel
import com.swenson.eventbuildinginc.ui.base.UiEvent
import com.swenson.eventbuildinginc.ui.base.UiState
import javax.annotation.concurrent.Immutable

@Immutable
sealed class MainScreenUiEvent : UiEvent {
    data class ShowData(val items: List<TaskCategoryUiModel>) : MainScreenUiEvent()
    data class OnChangeErrorVisibility(val showError: Boolean): MainScreenUiEvent()
    object Retry: MainScreenUiEvent()
}

@Immutable
data class MainScreenState(
    val isLoading: Boolean,
    val data: List<TaskCategoryUiModel>,
    val showError: Boolean,
) : UiState {
    companion object {
        fun initial() = MainScreenState(
            isLoading = true,
            data = emptyList(),
            showError = false
        )
    }

    override fun toString(): String {
        return "isLoading: $isLoading, data.size: ${data.size}"
    }
}