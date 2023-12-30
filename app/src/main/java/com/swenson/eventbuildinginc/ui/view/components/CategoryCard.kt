package com.swenson.eventbuildinginc.ui.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.TaskCategoryItem

@Composable
fun CategoryCard(
    item: TaskCategoryItem,
    onClick: (categoryId: Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { onClick(item.id) }
    ) {
        ConstraintLayout() {
            val (backgroundImage, categoryName, rightArrow) = createRefs()
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
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
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
                    .padding(end = 8.dp)
                    .constrainAs(rightArrow) {
                        top.linkTo(backgroundImage.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )

        }
    }
}