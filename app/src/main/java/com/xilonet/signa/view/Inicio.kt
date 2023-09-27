package com.xilonet.signa.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.xilonet.signa.R
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.model.LSMVideo
import com.xilonet.signa.model.UserInfo
import com.xilonet.signa.model.VideoFilesManager
import com.xilonet.signa.model.android.ExoPlayerManager
import com.xilonet.signa.view.theme.HeaderTitle
import com.xilonet.signa.view.theme.SignaBackground
import com.xilonet.signa.view.theme.SignaDark
import com.xilonet.signa.view.theme.SignaGreen
import com.xilonet.signa.view.theme.SignaLight
import com.xilonet.signa.view.theme.SignaYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


// CLASE SUSPENDIDA

@Composable
fun DiccionarioUI(context: Context, navController: NavController) {


    val userInfo by remember { mutableStateOf(HTTPUserManager.getUserInfo()) }

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val scaleFactor =
        if (isPortrait) 1f else 0.5f // Factor de escala del 50% en orientación horizontal





    Image(
        painter = painterResource(id = R.drawable.backa),
        contentDescription = null,
        modifier = Modifier
            .background(Color(0xFFE0E0E0))
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FullHeader(navController,
            categoryNames, category,
            { category = it },
            { searchQuery = it })
        if (category != "") {
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
               
                Spacer(modifier = Modifier.height(8.dp)) // Aplicar la escala al espacio

                Text(
                    text = "Invitado",
                    style = MaterialTheme.typography.h5,
                    color = Color.White
                )
            }
        }
        var searchQuery by remember { mutableStateOf("") }

        val voiceRecognitionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    // Handle the recognized text as needed
                    // For example, you can set it in the TextField
                    searchQuery = recognizedText
                }
            }

        }
        SearchBar(changeCategory, changeQuery, voiceRecognitionLauncher, searchQuery)
        // ...


        // ...
    }


    


    Spacer(modifier = Modifier.height(16.dp)) // Aplicar la escala al espacio


// Agregar el SearchBar debajo de los botones

// Agregar un botón para navegar a la pantalla de Diccionario si mostrarPantallaDiccionario es falso


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
private fun SearchBar(
    changeCategory: (String) -> Unit,
    changeQuery: (String) -> Unit,
    voiceRecognitionLauncher: ActivityResultLauncher<Intent>,
    searchQuery: String
) {
    var text by remember { mutableStateOf(searchQuery) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        TextField(
            value = text,
            onValueChange = {
                text = it
                val textString = it
                if (textString.isNotBlank()) {
                    changeCategory("")
                    changeQuery(textString)
                    ClosePreviousVideo()
                    ClosePreviousVideo = {}
                } else {
                    changeQuery("")
                }
                ScrollToTop()
            },
            textStyle = MaterialTheme.typography.subtitle1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_baseline_search_24),
                    contentDescription = null,
                    modifier = Modifier.height(24.dp),
                    alpha = 0.5f
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        )

        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                }
                voiceRecognitionLauncher.launch(intent)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Reconocimiento de voz",
                tint = SignaDark
            )
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
fun InicioUIi(navController: NavController) {
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

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp * scaleFactor) // Aplicar la escala a la altura
                .background(Color.Transparent)
        ) {
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
                        .size(80.dp * scaleFactor) // Aplicar la escala al tamaño de la imagen
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp * scaleFactor)) // Aplicar la escala al espacio
                Text(
                    text = userInfo?.firstName ?: "Invitado",
                    style = MaterialTheme.typography.h5,
                    color = Color.White
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp * scaleFactor)) // Aplicar la escala al espacio


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
        Spacer(Modifier.height(10.dp))
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

@Composable
private fun InicioButton(
    text: String,
    graphicBgColor: Color,
    icon: Painter,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Button(
        onClick = {
            onClick()
            isPressed = true
            // Restaurar el estado después de un breve retraso para la animación de pulsación.
            GlobalScope.launch {
                delay(100)
                isPressed = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp), clip = false),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, SignaDark),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isPressed) SignaBackground else SignaGreen,
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clickable { onClick() }
                .scale(scale) // Aplicar la animación de escala
                .align(Alignment.CenterVertically)
        ) {
            BackgroundGraphicWithLogo(icon = icon, bgColor = graphicBgColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
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