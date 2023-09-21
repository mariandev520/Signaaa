package com.xilonet.signa.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.xilonet.signa.R
import com.xilonet.signa.controller.Screen
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.model.VideoFilesManager
import com.xilonet.signa.view.theme.*

val SIDE_PADDING = 20.dp

@Composable
fun QuizCustomizerUI(context: Context, navController: NavController){
    val videoCategories = VideoFilesManager(context).getCategoryNames()
    val userInfo = HTTPUserManager.getUserInfo()
    val categoryTotalCount = videoCategories.size
    val categoryToIsSelected = remember {mutableStateMapOf<String, Boolean>().apply{
            putAll(videoCategories.associateWith { true })
        }}
    var selectedCategoriesCount = categoryToIsSelected.count { it.value }
    var enablePlay = selectedCategoriesCount > 0

    Column(horizontalAlignment = Alignment.Start) {
        FullHeader(navController, userInfo?.accumScore ?: 0)
        Column(modifier = Modifier.padding(horizontal = SIDE_PADDING)) {
            Spacer(Modifier.height(SIDE_PADDING))
            Row() {
                Text(
                    text = stringResource(R.string.incluir_categorias),
                    fontFamily = Poppins,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            CategorySelector(categoryToIsSelected)
            Spacer(Modifier.height(8.dp))
            SelectOrUnselectTwoButtons(categoryToIsSelected,
                selectedCategoriesCount, categoryTotalCount)
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ){
            PlayButton(enablePlay){
                var pipeSeparatedCategoryNames = ""
                categoryToIsSelected.filter {it.value}.keys.forEach {
                    pipeSeparatedCategoryNames += "$it|"
                }
                if(pipeSeparatedCategoryNames != "") {
                    pipeSeparatedCategoryNames =
                        pipeSeparatedCategoryNames.substring(0, pipeSeparatedCategoryNames.length-1)
                }
                navController.navigate(
                    Screen.Quiz.passPipeSeparatedCategories(pipeSeparatedCategoryNames)
                )
            }
            Spacer(Modifier.height(45.dp))
        }
    }
}

@Composable
private fun FullHeader(navController: NavController, accumScore: Int){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .requiredHeight(if(accumScore != 0) 120.dp else 60.dp)
            .fillMaxWidth()
            .background(SignaGreen)
    ) {
        Box() {
            HeaderTitle(stringResource(R.string.quiz))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(10.dp))
                BackButton(navController)
            }
        }
        Spacer(Modifier.height(8.dp))
        if(accumScore != 0) AccumScoreBanner(accumScore = accumScore)
    }
}

@Composable
private fun AccumScoreBanner(
    profilePic: Painter = painterResource(R.drawable.guest_user_profile_pic),
    accumScore: Int = 0
){
    Button(
        onClick = {/* TODO: Se podría mostrar un drop-down con más información */},
        colors = ButtonDefaults.buttonColors(SignaLight),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(40.dp),
        shape = RoundedCornerShape(40.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Image(
            painter = profilePic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, SignaDark, CircleShape),
            alignment = Alignment.CenterStart
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.puntaje_acumulado),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = accumScore.toString(),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun CategorySelector(categoriesToIsSelected: MutableMap<String, Boolean>){
    val sortedCategories = categoriesToIsSelected.toSortedMap()
    Row(){
        FlowRow(){
            sortedCategories.forEach{
                CategoryButton(it.key, it.value) { categoriesToIsSelected[it.key] = !it.value }
            }
        }
    }

}

@Composable
private fun CategoryButton(text: String,
                           selected: Boolean = true,
                           toggleMe: () -> Unit
){
    Column(){
        Row(){
            Button(
                onClick = {
                    toggleMe()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if(selected) SignaDark else SignaLight
                ),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(22.dp),
                shape = RoundedCornerShape(100),
                contentPadding = PaddingValues(0.dp)
            ){
                Image(
                    painter = painterResource(
                        if(selected){R.drawable.ic_baseline_check_circle_24}
                        else {R.drawable.ic_baseline_remove_circle_outline_24}
                    ),
                    null
                )
                Spacer(Modifier.width(2.dp))
                Text(text = text, style = MaterialTheme.typography.button,
                    color = if(selected) SignaLight else SignaDark)
                Spacer(Modifier.width(10.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SelectOrUnselectTwoButtons(categoryToIsSelected: MutableMap<String, Boolean>,
                               selectedCategoriesCount: Int,
                               totalCategoryCount: Int,
){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
        Box(Modifier.weight(1f)) {
            SelectOrUnselectAllGenericButton(
                selectAll = true,
                roundCornersLeft = true,
                enabled = selectedCategoriesCount < totalCategoryCount
            ) {
                categoryToIsSelected.forEach {
                    categoryToIsSelected[it.key] = true
                }
            }
        }
        Box(Modifier.weight(1f)) {
            SelectOrUnselectAllGenericButton(selectAll = false,
                roundCornersLeft = false,
                enabled = selectedCategoriesCount > 0
            ) {
                categoryToIsSelected.forEach {
                    categoryToIsSelected[it.key] = false
                }
            }
        }
    }
}

@Composable
private fun SelectOrUnselectAllGenericButton(selectAll: Boolean,
                                     roundCornersLeft: Boolean,
                                     enabled: Boolean,
                                     onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = SignaLight,
                                                disabledBackgroundColor = SignaLight),
        shape = if(roundCornersLeft){RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)}
                else {RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)},
        border = BorderStroke(1.dp, SignaDark),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = stringResource(if(selectAll) R.string.incluir_todas else R.string.quitar_todas),
            style = MaterialTheme.typography.body2, fontWeight = FontWeight.Bold,
            color = if(enabled) SignaDark else SignaDarkSemiTransparent
        )
    }
}

@Composable
private fun PlayButton(enabled: Boolean, onClick: () -> Unit){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = SignaRed,
        ),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(50.dp),
        shape = RoundedCornerShape(100),
        enabled = enabled
    ) {
        Text(text = stringResource(R.string.jugar), style = MaterialTheme.typography.body1,
            color = SignaLight)
    }
}
