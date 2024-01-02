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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EventRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher

): BaseViewModel<MainScreenState, MainScreenUiEvent>(){
    private val reducer = MainReducer(MainScreenState.initial())

    override val state: StateFlow<MainScreenState>
        get() = reducer.state



    fun changeErrorVisibilityState(show: Boolean){
        sendEvent(MainScreenUiEvent.OnChangeErrorVisibility(show))
    }

    fun getAllTasks() = viewModelScope.launch(ioDispatcher) {
        repository.fetchAllEvents().catch {
            it.printStackTrace()
            withContext(mainDispatcher){
                sendEvent(MainScreenUiEvent.OnChangeErrorVisibility(true))
            }
        }.collect { list ->
            withContext(mainDispatcher){
                sendEvent(MainScreenUiEvent.ShowData(list))
            }
        }
    }

    fun checkSavedEvents() = viewModelScope.launch {
        repository.hasUserSavedAtLeastOneEvent().collect {
            if (it == 1){
                sendEvent(MainScreenUiEvent.OpenEventSummaryScreen)
            } else {
                sendEvent(MainScreenUiEvent.RequestUserToSaveEvent)
            }
        }
    }


    private fun sendEvent(event: MainScreenUiEvent) {
        reducer.sendEvent(event)
    }

    private class MainReducer(initial: MainScreenState): BaseReducer<MainScreenState, MainScreenUiEvent>(initial) {
        override fun reduce(oldState: MainScreenState, event: MainScreenUiEvent) {
            when(event){
                is MainScreenUiEvent.ShowData -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            data = event.items.list,
                            averageBudget = event.items.averageBudget,
                            openEventSummary = false,
                            requestUserToSaveEvent = false
                        )
                    )
                }
                is MainScreenUiEvent.OnChangeErrorVisibility -> {
                    setState(
                        oldState.copy(
                            isLoading = !event.showError,
                            showError = event.showError,
                            openEventSummary = false,
                            requestUserToSaveEvent = false
                        )
                    )
                }
                is MainScreenUiEvent.Retry -> {
                    setState(
                        oldState.copy(
                            isLoading = true,
                            showError = false,
                            openEventSummary = false,
                            requestUserToSaveEvent = false
                        )
                    )
                }
                is MainScreenUiEvent.OpenEventSummaryScreen -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            showError = false,
                            openEventSummary = true,
                            requestUserToSaveEvent = false
                        )
                    )
                }
                is MainScreenUiEvent.RequestUserToSaveEvent -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            showError = false,
                            openEventSummary = false,
                            requestUserToSaveEvent = true,
                        )
                    )
                }
            }
        }

    }
}