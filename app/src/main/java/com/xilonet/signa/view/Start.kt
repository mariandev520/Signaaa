package com.xilonet.signa.view


import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.xilonet.signa.R

import com.xilonet.signa.model.HTTPUserManager

import com.xilonet.signa.model.VideoFilesManager
import com.xilonet.signa.model.android.ExoPlayerManager

import com.xilonet.signa.view.theme.SignaDark
import com.xilonet.signa.view.theme.SignaLight
import com.xilonet.signa.view.theme.SignaYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Vector

@Composable
fun DiccionariUI(context: Context, navController: NavController) {
    val userInfo by remember { mutableStateOf(HTTPUserManager.getUserInfo()) }

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val scaleFactor =
        if (isPortrait) 1f else 0.5f // Factor de escala del 50% en orientación horizontal





    Image(
        painter = painterResource(id = R.drawable.backa),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(1500.dp)
            .alpha(0.4f)
            .scale(scaleFactor), // Aplicar la escala al fondo
        contentScale = ContentScale.Crop
    )


    val videoFilesManager = VideoFilesManager(context)
    val exoPlayerManager = ExoPlayerManager(context)
    val categoryNames = videoFilesManager.getCategoryNames()

    var category by remember { mutableStateOf(categoryNames[0]) }
    var searchQuery by remember { mutableStateOf("") }



}


@Composable
private fun FullHeader(navController: NavController,
                       categoryNames: List<String>,
                       currentCategory: String,
                       changeCategory: (String) -> Unit,
                       changeQuery: (String) -> Unit
) {


    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) // Aplicar la escala a la altura
                .background(Color.Transparent)
        ) {
            // Contenido de la pantalla inicial
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.guest_user_profile_pic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp) // Aplicar la escala al tamaño de la imagen
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.height(8.dp )) // Aplicar la escala al espacio
                SearchBar(changeCategory, changeQuery)
                Text(
                    text = "Invitado",
                    style = MaterialTheme.typography.h5,
                    color = Color.White
                )
            }

        }
    }


    Spacer(modifier = Modifier.height(16.dp )) // Aplicar la escala al espacio



}







@Composable
private fun ButtonBelt(categoryNames: List<String>, currentCategory: String,
                       changeCategory: (String) -> Unit){
    LazyRow(){
        items(categoryNames) {
                category -> CategoryButton(category, category == currentCategory,
            changeCategory)
        }
        item {
            ButtonSpacer()
        }
    }
}

@Composable
private fun CategoryButton(text: String,
                           selected: Boolean = false,
                           changeCategory: (String) -> Unit
){
    Row(){
        ButtonSpacer()
        Button(
            onClick = {
                changeCategory(text)
                ClosePreviousVideo()
                ClosePreviousVideo = {}
                ScrollToTop()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(selected) SignaDark else SignaLight
            ),
            modifier = Modifier
                .width(108.dp)
                .height(22.dp),
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ){
            Text(text = text, style = MaterialTheme.typography.button,
                color = if(selected) SignaLight else SignaDark)
        }
    }
}

@Composable
private fun ButtonSpacer(){
    Spacer(Modifier.width(8.dp))
}

@Composable
private fun SearchBar(changeCategory: (String) -> Unit, changeQuery: (String) -> Unit){
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    TextField(value = text,
        onValueChange = {
            text = it
            val textString = text.text
            if(textString != ""){
                changeCategory("")
                changeQuery(textString)
                ClosePreviousVideo()
                ClosePreviousVideo = {}
            } else {
                changeQuery("")
            }
            ScrollToTop()
        },
        colors = TextFieldDefaults.textFieldColors(textColor = SignaDark,
            backgroundColor = SignaLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(0.dp),
        shape = RoundedCornerShape(100),
        textStyle = MaterialTheme.typography.subtitle1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        leadingIcon = {
            Image(
                painterResource(R.drawable.ic_baseline_search_24),
                null,
                modifier = Modifier.height(24.dp),
                alpha = 0.5f
            )
        },
    )
}

private lateinit var listState : LazyListState
private lateinit var coroutineScope : CoroutineScope

private fun ScrollToTop(){
    coroutineScope.launch {
        listState.animateScrollToItem(index = 0)
    }
}


@Composable
private fun VideoButtonRow(video1: VideoFilesManager.LSMVideo,
                           ctxt: Context,
                           exoPlayerManager: ExoPlayerManager,
                           scrollToMe: () -> Unit
) {
    Column(Modifier.padding(horizontal = SPACE_BETWEEN_VIDEOS)){
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
            VideoButton(video1.name, video1.path, ctxt, exoPlayerManager, scrollToMe)
        }
        Spacer(Modifier.height(SPACE_BETWEEN_VIDEOS))
    }
}

// We close the composable of the previous video (we must have only one at a time)
// We'll put the content inside brackets when we create the video we'll later close
private var ClosePreviousVideo = {}

@Composable
private fun VideoButton(videoName: String,
                        videoPath: String,
                        ctxt: Context,
                        exoPlayerManager: ExoPlayerManager,
                        scrollToMe: () -> Unit,
                        icon: Painter = painterResource(R.drawable.ic_baseline_play_arrow_24)
) {
    var videoOpen by remember {mutableStateOf(false)}
    Row(){
        Button(
            onClick = {
                if(!videoOpen){
                    scrollToMe()
                    ClosePreviousVideo()
                    ClosePreviousVideo = {videoOpen = false}
                }
                videoOpen = !videoOpen
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = SignaLight),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SignaDark)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(icon, null, Modifier.size(80.dp))
                Text(text = videoName, style = MaterialTheme.typography.body1, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(visible = videoOpen){
                    ImageOrVideoPlayer(ctxt, videoPath, exoPlayerManager)
                }
            }
        }
    }
}

@Composable
private fun ImageOrVideoPlayer(ctxt: Context, path: String, exoPlayerManager: ExoPlayerManager){
    // Detecta .jpg para imágenes
    if(path.substring(path.length-3).lowercase() == "jpg"){
        ImagePlayer(ctxt, path)
    } else {
        VideoPlayer(ctxt, path, exoPlayerManager)
    }
}

@Composable
private fun ImagePlayer(ctxt: Context, imagePath: String){
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ){
        Image(
            BitmapFactory.decodeStream(ctxt.assets.open(imagePath)).asImageBitmap(),
            null,
            modifier = Modifier
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
private fun VideoPlayer(ctxt: Context, videoPath: String, exoPlayerManager: ExoPlayerManager){
    val exoPlayer = remember(ctxt) { exoPlayerManager.getExoPlayer(videoPath) }

    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ){
        // Implementing ExoPlayer
        AndroidView(
            factory = {context -> StyledPlayerView(context).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }},
            modifier = Modifier
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}





@Composable
private fun UserInfoBanner(
    nameToDisplay: String = stringResource(R.string.guest),
    profilePic: Painter = painterResource(R.drawable.guest_user_profile_pic)
) {
    Button(
        onClick = {/* TODO: Se podría mostrar un drop-down con más información del usuario */ },
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
                .border(2.dp, SignaYellow, CircleShape),
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

