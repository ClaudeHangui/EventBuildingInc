package com.swenson.eventbuildinginc.data.remote

import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailItem
import com.swenson.eventbuildinginc.data.model.TaskCategoryItem
import retrofit2.http.GET
import retrofit2.http.Path

interface EventsApi {
    @GET("categories.json")
    suspend fun getTaskCategories(): List<TaskCategoryItem>?

    @GET("categories/{categoryId}.json")
    suspend fun getTaskCategoriesDetail(@Path("categoryId") movieId: Int): List<TaskCategoryDetailItem>?
}