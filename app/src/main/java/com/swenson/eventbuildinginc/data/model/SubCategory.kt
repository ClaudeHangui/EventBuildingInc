package com.swenson.eventbuildinginc.data.model

data class ParentCategoryBudgetRange(
    val overallMinBudget: String,
    val overallMaxBudget: String
)
abstract class CategoryDetailUiModel {
    abstract val budgetRange: ParentCategoryBudgetRange
}

data class ParentCategoryDetailUiModel(
    val subcategories: List<TaskCategoryDetailUiModel>,
    override val budgetRange: ParentCategoryBudgetRange
    ): CategoryDetailUiModel()

data class UpdateParentCategoryDetailUiModel(
    val saveCategoryEvent: Boolean,
    override val budgetRange: ParentCategoryBudgetRange
): CategoryDetailUiModel()