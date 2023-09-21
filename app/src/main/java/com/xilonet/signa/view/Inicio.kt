package com.xilonet.signa.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.material.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xilonet.signa.R
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.xilonet.signa.controller.Screen
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.model.UserInfo
import com.xilonet.signa.view.theme.*

@Composable
fun InicioUI(navController: NavController){
    val userInfo by remember {mutableStateOf(HTTPUserManager.getUserInfo())}
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FullHeader(userInfo)
        Spacer(Modifier.height(30.dp))
        LazyColumn(Modifier.fillMaxWidth(0.9f),
        ){
            item {
                InicioButton(
                    text = stringResource(R.string.diccionario),
                    graphicBgColor = SignaYellow,
                    icon = painterResource(id = R.drawable.library_icon),
                    onClick = {navController.navigate(Screen.Diccionario.route)}
                )
            }
            item {
                Spacer(Modifier.height(32.dp))
            }
            item {
                InicioButton(
                    text = stringResource(R.string.quiz),
                    graphicBgColor = SignaRed,
                    icon = painterResource(id = R.drawable.quiz_icon),
                    onClick = {navController.navigate(Screen.QuizCustomizer.route)}
                )
            }
        }
    }
}

@Composable
private fun FullHeader(userInfo: UserInfo?){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .requiredHeight(120.dp)
            .fillMaxWidth()
            .background(SignaGreen)
    ) {
        HeaderTitle(stringResource(R.string.inicio))
        Spacer(Modifier.height(8.dp))
        val fullUserName = if(userInfo != null) {
                                userInfo.firstName + " " + userInfo.lastName
                            } else {
                                stringResource(R.string.guest)
                            }
        UserInfoBanner(nameToDisplay = fullUserName)
        Log.d("LOGIN", fullUserName)
    }
}

@Composable
private fun UserInfoBanner(
    nameToDisplay: String = stringResource(R.string.guest),
    profilePic: Painter = painterResource(R.drawable.guest_user_profile_pic)
){
    Button(
        onClick = {/* TODO: Se podría mostrar un drop-down con más información del usuario */},
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
            text = nameToDisplay,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start,
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun InicioButton(text: String,
                 graphicBgColor: Color,
                 icon: Painter,
                 onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = SignaLight),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, SignaDark),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BackgroundGraphicWithLogo(icon = icon, bgColor = graphicBgColor)
            Text(text = text, style = MaterialTheme.typography.body1, fontSize = 24.sp)
        }
    }
}

@Composable
private fun BackgroundGraphicWithLogo(size: Dp = 128.dp,
                                      icon: Painter,
                                      bgColor: Color
){
    Box(){
        Image(
            painterResource(R.drawable.background_pentagon),
            null,
            Modifier.size(size),
            colorFilter = ColorFilter.tint(bgColor)
        )
        Image(
            icon,
            null,
            Modifier
                .zIndex(1f)
                .size(size)
                .padding(0.dp, 0.dp, 0.dp, 5.dp)
        )
    }
}