package com.swenson.eventbuildinginc.ui.presentation

import com.swenson.eventbuildinginc.data.model.OverallBudgetRange
import com.swenson.eventbuildinginc.ui.base.UiEvent
import com.swenson.eventbuildinginc.ui.base.UiState
import javax.annotation.concurrent.Immutable

@Immutable
sealed class EstimatedBudgetUiEvent: UiEvent {
    data class ShowEstimate(val estimate: OverallBudgetRange): EstimatedBudgetUiEvent()
}

@Immutable
data class EstimatedBudgetState(
    val isLoading: Boolean,
    val budget: OverallBudgetRange? = null
): UiState {
    companion object {
        fun init() = EstimatedBudgetState(
            isLoading = true,
            budget = null
        )
    }

    override fun toString(): String {
        return "budget: $budget, isLoading: $isLoading"
    }
}