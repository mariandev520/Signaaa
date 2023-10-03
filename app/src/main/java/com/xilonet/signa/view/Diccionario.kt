package com.xilonet.signa.view

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.xilonet.signa.R
import com.xilonet.signa.controller.Screen
import com.xilonet.signa.model.LSMVideo
import com.xilonet.signa.model.VideoFilesManager
import com.xilonet.signa.model.android.ExoPlayerManager
import com.xilonet.signa.view.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DiccionarioUI(context: Context, navController: NavController){
    val videoFilesManager = VideoFilesManager(context)
    val exoPlayerManager = ExoPlayerManager(context)
    val categoryNames = videoFilesManager.getCategoryNames()

    var category by remember {mutableStateOf(categoryNames[0])}
    var searchQuery by remember {mutableStateOf("")}

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FullHeader(navController,
            categoryNames, category,
            { category = it },
            { searchQuery = it})

        if(category != ""){
            VideoGrid(videoFilesManager.getVideosOfCategory(category), context, exoPlayerManager)
        } else {
            VideoGrid(videoFilesManager.search(searchQuery), context, exoPlayerManager)
        }
    }
}

@Composable
private fun FullHeader(navController: NavController,
                       categoryNames: List<String>,
                       currentCategory: String,
                       changeCategory: (String) -> Unit,
                       changeQuery: (String) -> Unit
){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(SignaGreen)
    ) {
        Box() {
            HeaderTitle(stringResource(R.string.diccionario))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(10.dp))
                BackButton(navController)
            }
        }
        Spacer(Modifier.height(4.dp))
        SearchBar(changeCategory, changeQuery)
        Spacer(Modifier.height(12.dp))
        ButtonBelt(categoryNames, currentCategory, changeCategory)
        Spacer(Modifier.height(12.dp))
    }
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
    var searchQueryText by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    TextField(value = text,
        onValueChange = {
                            text = it
                            val textString = text.text
                            searchQueryText = textString
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
    Card {
        Text(
            text = if (searchQueryText.isNotEmpty()) {
                "Frase Hecha: $searchQueryText"
            } else {
                "Frase a traducir"
            },
            modifier = Modifier
                .padding(10.dp)
                .height(60.dp),
        )
        Button(
            onClick = { /* Traducción */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(80.dp)
        ) {
            Text(text = "Traducir")
        }
    }
}

private lateinit var listState : LazyListState
private lateinit var coroutineScope : CoroutineScope

private fun ScrollToTop(){
    coroutineScope.launch {
        listState.animateScrollToItem(index = 0)
    }
}

val SPACE_BETWEEN_VIDEOS = 30.dp

@Composable
private fun VideoGrid(videosToShow: List<LSMVideo>,
                      ctxt: Context,
                      exoPlayerManager: ExoPlayerManager
) {
    val offset = with(LocalDensity.current) { -SPACE_BETWEEN_VIDEOS.roundToPx() }

    listState = rememberLazyListState()
    coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState){
        item {
            Spacer(Modifier.height(SPACE_BETWEEN_VIDEOS))
        }
        itemsIndexed(videosToShow){
            index, video -> VideoButtonRow(video, ctxt, exoPlayerManager) {
                coroutineScope.launch {
                    listState.animateScrollToItem(index = index+1, scrollOffset = offset)
                }
            }
        }
    }
}

@Composable
private fun VideoButtonRow(video1: LSMVideo,
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
    Box(modifier = Modifier.clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ){
        Image(
            BitmapFactory.decodeStream(ctxt.assets.open(imagePath)).asImageBitmap(),
            null,
            modifier = Modifier.aspectRatio(1.35f).clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
private fun VideoPlayer(ctxt: Context, videoPath: String, exoPlayerManager: ExoPlayerManager){
    val exoPlayer = remember(ctxt) { exoPlayerManager.getExoPlayer(videoPath) }

    Box(modifier = Modifier.clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ){
        // Implementing ExoPlayer
        AndroidView(
            factory = {context -> StyledPlayerView(context).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }},
            modifier = Modifier.aspectRatio(1.35f).clip(RoundedCornerShape(16.dp))
        )
    }
}
