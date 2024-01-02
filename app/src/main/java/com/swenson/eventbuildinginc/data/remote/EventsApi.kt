package com.swenson.eventbuildinginc.data.remote

import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailRemote
import com.swenson.eventbuildinginc.data.model.TaskCategoryRemote
import retrofit2.http.GET
import retrofit2.http.Path

interface EventsApi {
    @GET("categories.json")
    suspend fun getTaskCategories(): List<TaskCategoryRemote>?

    @GET("categories/{categoryId}.json")
    suspend fun getTaskCategoriesDetail(@Path("categoryId") movieId: Int): List<TaskCategoryDetailRemote>?
}