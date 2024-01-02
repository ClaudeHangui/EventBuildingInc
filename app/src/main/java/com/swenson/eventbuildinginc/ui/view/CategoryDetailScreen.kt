@file:OptIn(ExperimentalFoundationApi::class)

package com.swenson.eventbuildinginc.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailUiModel
import com.swenson.eventbuildinginc.ui.presentation.CategoryDetailViewModel
import com.swenson.eventbuildinginc.ui.view.components.CategoryDetailsCard
import com.swenson.eventbuildinginc.ui.view.components.ContentWithProgress

@Composable
fun CategoryDetailScreen(
    categoryId: Int,
    categoryName: String,
    navigateBack: () -> Unit,
    viewModel: CategoryDetailViewModel = hiltViewModel()
    ){

    val myScaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.getAllSubcategories(categoryId)
    }

    Scaffold(scaffoldState = myScaffoldState) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .background(Color(0xFFFAF9F8))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = 48.dp)
            ){
                BackStackButton {
                    navigateBack()
                }

                Text(
                    text = categoryName,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = MaterialTheme.typography.h3.fontFamily,
                    ),
                )

                Text(
                    text = "Add to your event to view our cost estimate",
                    style = TextStyle(
                        color = Color(0xFF747474),
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = MaterialTheme.typography.h6.fontFamily,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 6.dp, start = 36.dp, end = 36.dp)
                )
                println("state : $state")
                when {
                    state.isLoading -> ContentWithProgress()
                    state.data.isNotEmpty() -> {
                       CategoryDetailItem(
                           categoryId,
                           state.overallMinBudget,
                           state.overAllMaxBudget,
                           subCategories = state.data,
                           onIconCheckedChanged = { index, catId, parentId, addItemToList ->
                               viewModel.onItemIconChanged(index, catId, parentId, addItemToList)
                           }
                       )
                    }
                    state.showError -> ErrorScreen {
                        viewModel.changeErrorVisibilityState(false)
                        viewModel.getAllSubcategories(categoryId)
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.BackStackButton(onCLick: () -> Unit) {
    IconButton(
        onClick = { onCLick() },
        modifier = Modifier
            .align(Alignment.Start)
            .wrapContentWidth()
            .wrapContentHeight())
    {
        Icon(
            painter = painterResource(id = R.drawable.left_arrow_black),
            contentDescription = null
        )
    }
}

@Composable
fun ColumnScope.ErrorScreen(onRetry: () -> Unit){
    Text(
        text = stringResource(id = R.string.default_error),
        style = TextStyle(
            color = Color.Black,
            fontSize = 24.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MaterialTheme.typography.h3.fontFamily,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 92.dp)
    )

    val mainButtonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color.DarkGray,
        contentColor = MaterialTheme.colors.onSurface
    )

    Button(
        colors = mainButtonColor,
        onClick = {
            onRetry()
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.retry),
            color = Color.White,
            modifier = Modifier.padding(
                start = 10.dp,
                end = 10.dp
            )
        )
    }
}

@Composable
private fun CategoryDetailItem(
    parentCategory: Int,
    minBudget: String,
    maxBudget: String,
    subCategories: List<TaskCategoryDetailUiModel>,
    onIconCheckedChanged: (Int, Int, Int, Boolean) -> Unit
){
    val lazyListState = rememberLazyStaggeredGridState()

    Box {
        
        Column() {
            Text(
                text = if (minBudget.isEmpty() && maxBudget.isEmpty()) "-" else stringResource(
                    id = R.string.budget_range,
                    minBudget,
                    maxBudget
                ),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 36.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MaterialTheme.typography.h3.fontFamily,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 36.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                state = lazyListState,
            ) {
                itemsIndexed(items = subCategories) { index, subTask ->
                    CategoryDetailsCard(item = subTask, index, onIconCheckedChanged, parentCategory)
                }
            }
        }
    }
}