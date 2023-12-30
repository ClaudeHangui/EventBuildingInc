package com.swenson.eventbuildinginc.ui.presentation

sealed class MainScreenItem {
    object SaveTaskButton: MainScreenItem()
    object UpdateAverageAmount: MainScreenItem()
    object UpdateTaskCountSaved: MainScreenItem()
}