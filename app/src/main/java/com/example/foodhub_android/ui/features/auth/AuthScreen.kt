package com.example.foodhub_android.ui.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub_android.R
import com.example.foodhub_android.ui.theme.Orange

@Composable
fun AuthScreen() {
    val imageSize = remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush = Brush.verticalGradient(
        colors = listOf(
            androidx.compose.ui.graphics.Color.Transparent,
            androidx.compose.ui.graphics.Color.Black
        ),
        startY = imageSize.value.height.toFloat() / 3,

        )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier
                .onGloballyPositioned {
                    imageSize.value = it.size
                }
                .alpha(0.6f)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush = brush)
        )
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.Skip), color = Orange)
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 110.dp)){
            Text(
                text = stringResource(id = R.string.Welcome),
                color = Color.Black,
                fontSize = 50.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold


            )
            Text(
                text = stringResource(id = R.string.food_hub),
                color = Orange,
                fontSize = 50.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold

            )


        }

    }

}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}