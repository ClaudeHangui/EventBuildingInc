package com.swenson.eventbuildinginc.ui.presentation

import android.util.Log
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
        var collectCount = 0
        repository.fetchAllEvents().catch {
            println("throwable: $it")
            it.printStackTrace()
            sendEvent(MainScreenUiEvent.OnChangeErrorVisibility(true))
        }.collectLatest { list ->
            println("collect count: ${++collectCount}")
            sendEvent(MainScreenUiEvent.ShowData(list))
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
                            data = event.items
                        )
                    )
                }
                is MainScreenUiEvent.OnChangeErrorVisibility -> {
                    setState(
                        oldState.copy(
                            isLoading = !event.showError,
                            showError = event.showError
                        )
                    )
                }
                is MainScreenUiEvent.Retry -> {
                    setState(
                        oldState.copy(
                            isLoading = true,
                            showError = false
                        )
                    )
                }
                else -> {
                    //
                }
            }
        }

    }
}