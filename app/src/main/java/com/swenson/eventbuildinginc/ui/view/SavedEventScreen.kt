package com.swenson.eventbuildinginc.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.OverallBudgetRange
import com.swenson.eventbuildinginc.ui.presentation.EstimatedBudgetViewModel
import com.swenson.eventbuildinginc.ui.view.components.BackPressHandler
import com.swenson.eventbuildinginc.ui.view.components.ContentWithProgress

@Composable
fun SavedEventScreen(
    navigateBack: () -> Unit,
    viewModel: EstimatedBudgetViewModel = hiltViewModel()
    ){

    val myScaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackPressHandler(onBackPressed = {
        navigateBack()
    })

    Scaffold(scaffoldState = myScaffoldState) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .background(Color(0xFFFAF9F8))
        ){
            when {
                state.isLoading -> ContentWithProgress()
                state.budget != null -> BudgetEstimateSection(overallBudgetRange = state.budget!!)
            }
        }
    }
}

@Composable
fun BudgetEstimateSection(
    overallBudgetRange: OverallBudgetRange
) {
    Box {
        ConstraintLayout() {
            val (headerTitle, budgetRange, starIcon, whiteCircle) = createRefs()
            Image(
                painterResource(R.drawable.white_circle),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .constrainAs(whiteCircle) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = stringResource(id = R.string.event_saved),
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 20.dp)
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(top = 32.dp)
                    .constrainAs(headerTitle) {
                        top.linkTo(whiteCircle.top)
                        start.linkTo(whiteCircle.start)
                        end.linkTo(whiteCircle.end)
                    }
            )

            Text(
                text = stringResource(
                    id = R.string.budget_range,
                    overallBudgetRange.minBudget,
                    overallBudgetRange.maxBudget
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
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 16.dp)
                    .constrainAs(budgetRange) {
                        top.linkTo(headerTitle.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Image(
                painterResource(R.drawable.ic_black_star),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(40.dp)
                    .height(40.dp)
                    .constrainAs(starIcon) {
                        top.linkTo(budgetRange.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

        }
    }
}