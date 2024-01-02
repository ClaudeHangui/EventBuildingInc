package com.swenson.eventbuildinginc.ui.view.components

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.swenson.eventbuildinginc.R
import com.swenson.eventbuildinginc.data.model.TaskCategoryDetailUiModel

@Composable
fun CategoryDetailsCard(
    item: TaskCategoryDetailUiModel,
    indexPosition: Int,
    onIconCheckedChanged: (Int, Int, Int, Boolean) -> Unit,
    parentCategory: Int, ){
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        ConstraintLayout {
            val (backgroundImage, subCategoryName, avgAmount, currentIcon) = createRefs()
            AsyncImage(
                model = item.image,
                contentDescription = "sub category image",
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
                    .constrainAs(subCategoryName) {
                        top.linkTo(backgroundImage.bottom)
                        start.linkTo(parent.start)
                    }
            )

            val min = String.format("%,d", item.minBudget)
            val max = String.format("%,d", item.maxBudget)

            Text(
                text = stringResource(id = R.string.budget_range, min, max),
                style = TextStyle(
                    color = Color(0xFF616161),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MaterialTheme.typography.h6.fontFamily,
                ),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .constrainAs(avgAmount) {
                        top.linkTo(subCategoryName.bottom)
                        start.linkTo(subCategoryName.start)
                        bottom.linkTo(parent.bottom)
                    }
            )

            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, end = 12.dp)
                    .clip(CircleShape)
                    .background(Color(R.color.semi_black))
                    .clickable {
                        onIconCheckedChanged(indexPosition, item.id, parentCategory, !item.isItemSelected)
                    }
                    .padding(8.dp)
                    .constrainAs(currentIcon) {
                        top.linkTo(backgroundImage.top)
                        end.linkTo(backgroundImage.end)
                    }
            ){
                val iconRes = if (item.isItemSelected) R.drawable.white_tick else R.drawable.white_cross
                Icon(
                    tint = Color.White,
                    painter = painterResource(id = iconRes),
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    contentDescription = null)
            }
        }
    }
}