package com.swenson.eventbuildinginc.ui.presentation

sealed class Destinations (val route: String){
    object Categories: Destinations("categories")
    object CategoryDetails: Destinations("category_details"){
        val categoryId = "categoryId"
        val categoryName = "categoryName"
    }
    object SavedEvent: Destinations("saved_event")
    fun withArgsFormat(vararg args: String) : String {
        return buildString {
            append(route)
            args.forEach{ arg ->
                append("/{$arg}")
            }
        }
    }
}
