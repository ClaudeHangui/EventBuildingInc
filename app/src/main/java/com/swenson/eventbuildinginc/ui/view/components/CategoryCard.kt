package com.swenson.eventbuildinginc.ui.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import coil.compose.AsyncImage
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.TaskCategoryUiModel

@Composable
fun CategoryCard(
    item: TaskCategoryUiModel,
    onClick: (categoryId: Int) -> Unit
) {
    val count = item.subcategoriesSelectedCount

    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = 8.dp,
        border = BorderStroke(2.dp, if (count <= 0) Color(0x00000000)
        else Color(0xFF5DA3A9)),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { onClick(item.id) }
    ) {
        ConstraintLayout() {
            val (backgroundImage, subcategoriesSelectedCount, categoryName, rightArrow) = createRefs()
            AsyncImage(
                model = item.image,
                contentDescription = "category image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(0.5.dp)
                    .fillMaxWidth()
                    .constrainAs(backgroundImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .height(105.dp),
            )

            Box(modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(top = 12.dp, end = 12.dp)
                .clip(CircleShape)
                .background(Color(0xFF5DA3A9))
                .padding(8.dp)
                .constrainAs(subcategoriesSelectedCount) {
                    top.linkTo(backgroundImage.top)
                    end.linkTo(backgroundImage.end)
                    visibility = if (count <= 0) {
                        Visibility.Gone
                    } else {
                        Visibility.Visible
                    }
                }
            ){
                Text(
                    text = if (count < 10) "0".plus(count) else count.toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = MaterialTheme.typography.h6.fontFamily
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                )
            }

            Text(
                text = item.title,
                style = TextStyle(
                    color = Color(0xFF616161),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MaterialTheme.typography.h6.fontFamily,
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, bottom = 12.dp, end = 8.dp)
                    .constrainAs(categoryName) {
                        top.linkTo(backgroundImage.bottom)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )

            Image(
                painterResource(R.drawable.right_arrow_green),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(end = 12.dp)
                    .constrainAs(rightArrow) {
                        top.linkTo(backgroundImage.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )

        }
    }
}