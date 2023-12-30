package com.swenson.eventbuildinginc.data.model

data class SubCategory(
    val id: Int,
    val avgBudget: Int,
    val image: String,
    val maxBudget: Int,
    val minBudget: Int,
    val title: String,
    val isCategorySaved: Boolean
)