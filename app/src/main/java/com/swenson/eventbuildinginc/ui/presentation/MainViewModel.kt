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

    init {
        getAllTasks()
    }

    fun changeErrorVisibilityState(show: Boolean){
        sendEvent(MainScreenUiEvent.OnChangeErrorVisibility(show))
    }

    fun getAllTasks() = viewModelScope.launch(ioDispatcher){
        val result = when(val categoriesResult = repository.fetchAllCategories()){
            is Resource.Success -> {
                MainScreenUiEvent.ShowData(categoriesResult.data!!)
            }
            is Resource.Error -> {
                if (categoriesResult.data.isNullOrEmpty()){
                    MainScreenUiEvent.OnChangeErrorVisibility(true)
                } else {
                    MainScreenUiEvent.ShowData(categoriesResult.data)
                }
            }
        }
        withContext(mainDispatcher){
            sendEvent(result)
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