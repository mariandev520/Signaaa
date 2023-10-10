package com.xilonet.signa.view

import android.content.Context
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.xilonet.signa.R
import com.xilonet.signa.model.LSMVideo
import com.xilonet.signa.model.VideoFilesManager
import com.xilonet.signa.model.android.ExoPlayerManager
import com.xilonet.signa.view.theme.BackButton
import com.xilonet.signa.view.theme.HeaderTitle
import com.xilonet.signa.view.theme.SignaDark
import com.xilonet.signa.view.theme.SignaGreen
import com.xilonet.signa.view.theme.SignaLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DiccionarioUI(context: Context, navController: NavController, onCloseClick: () -> Unit) {
    val videoFilesManager = VideoFilesManager(context)
    val categoryNames = videoFilesManager.getCategoryNames()
    var category by remember { mutableStateOf(categoryNames[0]) }
    var searchQuery by remember { mutableStateOf("") }
    var isVideoPlaying by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var searchQueryText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val exoPlayerManager = remember { ExoPlayerManager(context) }
    var isCardVisible by remember { mutableStateOf(true) }
    var videoVisible by remember { mutableStateOf(false) }
    var isVideoOpen by remember { mutableStateOf(false) }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FullHeader(
            navController,
            categoryNames,
            currentCategory = category,
            changeCategory = { category = it },
            changeQuery = { searchQuery = it },
            onCloseClick = onCloseClick
        )
        Card {
                Text(
                    text = if (searchQueryText.isNotEmpty()) {
                        "Frase Hecha: $searchQueryText"
                    } else {
                        " "
                    },
                    modifier = Modifier
                        .padding(2.dp)
                        .height(20.dp),
                )

                Button(
                    onClick = {
                        isVideoOpen = !isVideoOpen
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(80.dp)
                ) {
                    Text(text = "Traducir")
                }

                AnimatedVisibility(visible = isVideoOpen) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            item {
                                Box (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ){
                                    IconButton(
                                        onClick = {
                                            isVideoOpen = false // Actualizar el valor de isVideoOpen
                                            onCloseClick()
                                        },
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colors.primary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Cerrar",
                                        )
                                    }
                                }
                            }
                            item {
                                VideoPlayer(
                                    context,
                                    videoPath = "lsm/Saludos/Hola.m4v",
                                    exoPlayerManager
                                )
                            }
                        }
                    }
                }
            }
            if (category != "") {
                VideoGrid(videoFilesManager.getVideosOfCategory(category), context, exoPlayerManager)
            } else {
                VideoGrid(videoFilesManager.search(searchQuery), context, exoPlayerManager)
            }
        }
    }

@Composable
private fun FullHeader(
    navController: NavController,
    categoryNames: List<String>,
    currentCategory: String,
    changeCategory: (String) -> Unit,
    changeQuery: (String) -> Unit,
    onCloseClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(SignaGreen)
    ) {
        Box {
            HeaderTitle(stringResource(R.string.diccionario))
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                BackButton(navController)
            }
        }
        Spacer(Modifier.height(4.dp))
        SearchBar(changeCategory, changeQuery, onCloseClick)
        Spacer(Modifier.height(12.dp))
        ButtonBelt(categoryNames, currentCategory, changeCategory)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ButtonBelt(
    categoryNames: List<String>, currentCategory: String,
    changeCategory: (String) -> Unit
) {
    LazyRow {
        items(categoryNames) { category ->
            CategoryButton(
                category, category == currentCategory,
                changeCategory
            )
        }
        item {
            ButtonSpacer()
        }
    }
}

@Composable
private fun CategoryButton(
    text: String,
    selected: Boolean = false,
    changeCategory: (String) -> Unit
) {
    Row {
        ButtonSpacer()
        Button(
            onClick = {
                changeCategory(text)
                ClosePreviousVideo()
                ClosePreviousVideo = {}
                ScrollToTop()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selected) SignaDark else SignaLight
            ),
            modifier = Modifier
                .width(108.dp)
                .height(22.dp),
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = text, style = MaterialTheme.typography.button,
                color = if (selected) SignaLight else SignaDark
            )
        }
    }
}

@Composable
private fun ButtonSpacer() {
    Spacer(Modifier.width(8.dp))
}

@Composable
private fun SearchBar(
    changeCategory: (String) -> Unit, changeQuery: (String) -> Unit, onCloseClick: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var searchQueryText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current
    TextField(
        value = text,
        onValueChange = {
            text = it
            val textString = text.text
            searchQueryText = textString
            if (textString != "") {
                changeCategory("")
                changeQuery(textString)
                ClosePreviousVideo()
                ClosePreviousVideo = {}
            } else {
                changeQuery("")
            }
            ScrollToTop()
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = SignaDark,
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

private lateinit var listState: LazyListState
private lateinit var coroutineScope: CoroutineScope

private fun ScrollToTop() {
    coroutineScope.launch {
        listState.animateScrollToItem(index = 0)
    }
}

val SPACE_BETWEEN_VIDEOS = 30.dp

@Composable
private fun VideoGrid(
    videosToShow: List<LSMVideo>,
    ctxt: Context,
    exoPlayerManager: ExoPlayerManager
) {
    val offset = with(LocalDensity.current) { -SPACE_BETWEEN_VIDEOS.roundToPx() }

    listState = rememberLazyListState()
    coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState) {
        item {
            Spacer(
                Modifier
                    .padding(100.dp)
                    .height(SPACE_BETWEEN_VIDEOS)
            )
        }
        itemsIndexed(videosToShow) { index, video ->
            VideoButtonRow(video, ctxt, exoPlayerManager) {
                coroutineScope.launch {
                    listState.animateScrollToItem(index = index + 1, scrollOffset = offset)
                }
            }
        }
    }
}

@Composable
private fun VideoButtonRow(
    video1: LSMVideo,
    ctxt: Context,
    exoPlayerManager: ExoPlayerManager,
    scrollToMe: () -> Unit
) {
    Column(Modifier.padding(horizontal = SPACE_BETWEEN_VIDEOS)) {
        Row(
            horizontalArrangement = Arrangement.Start, modifier = Modifier
                .fillMaxWidth()
        ) {
            VideoButton(video1.name, video1.path, ctxt, exoPlayerManager, scrollToMe)
        }
        Spacer(Modifier.height(SPACE_BETWEEN_VIDEOS))
    }
}

// We close the composable of the previous video (we must have only one at a time)
// We'll put the content inside brackets when we create the video we'll later close
private var ClosePreviousVideo = {}

@Composable
private fun VideoButton(
    videoName: String,
    videoPath: String,
    ctxt: Context,
    exoPlayerManager: ExoPlayerManager,
    scrollToMe: () -> Unit,
    icon: Painter = painterResource(R.drawable.ic_baseline_play_arrow_24)
) {
    var videoOpen by remember { mutableStateOf(false) }
    Row {
        Button(
            onClick = {
                if (!videoOpen) {
                    scrollToMe()
                    ClosePreviousVideo()
                    ClosePreviousVideo = { videoOpen = false }
                }
                videoOpen = !videoOpen
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = SignaLight),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SignaDark)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(icon, null, Modifier.size(80.dp))
                Text(text = videoName, style = MaterialTheme.typography.body1, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(visible = videoOpen) {
                    ImageOrVideoPlayer(ctxt, videoPath, exoPlayerManager)
                }
            }
        }
    }
}

@Composable
private fun ImageOrVideoPlayer(ctxt: Context, path: String, exoPlayerManager: ExoPlayerManager) {
    // Detecta .jpg para imágenes
    if (path.substring(path.length - 3).lowercase() == "jpg") {
        ImagePlayer(ctxt, path)
    } else {
        VideoPlayer(ctxt, videoPath = path, exoPlayerManager)
    }
}

@Composable
private fun ImagePlayer(ctxt: Context, imagePath: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ) {
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
private fun VideoPlayer(ctxt: Context, videoPath: String, exoPlayerManager: ExoPlayerManager) {
    val exoPlayer = remember(ctxt) { exoPlayerManager.getExoPlayer(videoPath) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
    ) {
        // Implementing ExoPlayer
        AndroidView(
            factory = { context ->
                StyledPlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}
