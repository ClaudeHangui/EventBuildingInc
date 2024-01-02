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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class EstimatedBudgetViewModel @Inject constructor(
    private val repository: EventRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher

    ): BaseViewModel<EstimatedBudgetState, EstimatedBudgetUiEvent>() {

    private val reducer = EstimatedBudgetReducer(EstimatedBudgetState.init())

    override val state: StateFlow<EstimatedBudgetState>
        get() = reducer.state

    init {
        getOverallBudgetEstimate()
    }

    private fun getOverallBudgetEstimate() = viewModelScope.launch(ioDispatcher) {
        val result = repository.getOverallBudgetEstimateForSelectedItems()
        withContext(mainDispatcher){
            reducer.sendEvent(EstimatedBudgetUiEvent.ShowEstimate(result))
        }
    }

    private class EstimatedBudgetReducer(default: EstimatedBudgetState): BaseReducer<EstimatedBudgetState, EstimatedBudgetUiEvent>(default){
        override fun reduce(oldState: EstimatedBudgetState, event: EstimatedBudgetUiEvent) {
            when(event){
                is EstimatedBudgetUiEvent.ShowEstimate -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            budget = event.estimate
                        )
                    )
                }
            }

        }

    }
}