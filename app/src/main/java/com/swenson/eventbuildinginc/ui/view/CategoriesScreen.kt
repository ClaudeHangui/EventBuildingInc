
package com.swenson.eventbuildinginc.ui.view

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.TaskCategoryUiModel
import com.swenson.eventbuildinginc.ui.presentation.Destinations
import com.swenson.eventbuildinginc.ui.presentation.MainViewModel
import com.swenson.eventbuildinginc.ui.view.components.CategoryCard
import com.swenson.eventbuildinginc.ui.view.components.ContentWithProgress

@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val myScaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lifecycleEvent = rememberLifecycleEvent()
    LaunchedEffect(key1 = lifecycleEvent, key2 = state.requestUserToSaveEvent, key3 = state.openEventSummary) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            // initiate data reloading
            viewModel.getAllTasks()
        }

        if (state.requestUserToSaveEvent){
            Toast.makeText(context, context.getString(R.string.save_event), Toast.LENGTH_SHORT).show()
        }

        if (state.openEventSummary){
            navController.navigate(Destinations.SavedEvent.route)
        }
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
                    .padding(start = 8.dp, end = 8.dp, top = 56.dp)
            ) {
                Text(
                    text = "Event Builder",
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
                        CategoryListSection(
                            overallAverageBudget = state.averageBudget,
                            categories = state.data,
                            onCategoryClick = { categoryId, name ->
                                navController.navigate(
                                    Destinations.CategoryDetails.route + "/${categoryId}/${name}"
                                )
                            }
                        )
                    }
                    state.showError -> {
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
                                viewModel.changeErrorVisibilityState(false)
                                viewModel.getAllTasks()
                            },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "RETRY",
                                color = Color.White,
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    end = 10.dp
                                )
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.checkSavedEvents()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5DA3A9)),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)

            ) {
                Text(
                    text = "Save",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 8.dp, bottom = 8.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.h4.fontFamily,
                    )
                )
            }
        }
    }
}

@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            state = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return state
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryListSection(
    overallAverageBudget: String,
    categories: List<TaskCategoryUiModel>,
    onCategoryClick: (Int, String) -> Unit
) {
    val lazyListState = rememberLazyStaggeredGridState()
    Box(
        contentAlignment = Alignment.Center
    ) {
        Column() {
            Text(
                text = if (overallAverageBudget.isEmpty()) "-" else stringResource(
                    id = R.string.average_budget, overallAverageBudget
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
                    .padding(top = 32.dp)

                ,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                state = lazyListState,
            ) {
                items(items = categories) { task ->
                    CategoryCard(item = task){
                        onCategoryClick(task.id, task.title)
                    }
                }
            }
        }
    }
}