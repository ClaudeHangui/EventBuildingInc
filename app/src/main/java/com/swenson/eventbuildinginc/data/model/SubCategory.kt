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

data class ParentCategoryBudgetRange(
    val overallMinBudget: String,
    val overallMaxBudget: String
)
abstract class CategoryDetailUiModel {
    abstract val budgetRange: ParentCategoryBudgetRange
}

data class ParentCategoryDetailUiModel(
    val subcategories: List<SubCategory>,
    override val budgetRange: ParentCategoryBudgetRange
    ): CategoryDetailUiModel()

data class UpdateParentCategoryDetailUiModel(
    val saveCategoryEvent: Boolean,
    override val budgetRange: ParentCategoryBudgetRange
): CategoryDetailUiModel()