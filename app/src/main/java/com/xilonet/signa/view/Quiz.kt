package com.xilonet.signa.view

import android.content.Context
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.xilonet.signa.R
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.model.QuizVideoRandomSelector
import com.xilonet.signa.model.android.ExoPlayerManager
import com.xilonet.signa.view.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.util.*

enum class ScreenMode {
    PLAY, TIME_OUT, GAME_OVER, CORRECT, INCORRECT, ALL_FINISHED
}

private lateinit var coroutineScope: CoroutineScope

@Composable
fun QuizUI(context: Context, navController: NavController, categories: List<String>) {
    coroutineScope = rememberCoroutineScope()
    val exoPlayerManager by remember {mutableStateOf(ExoPlayerManager(context))}
    val randomSelector by remember {mutableStateOf(QuizVideoRandomSelector(context, categories))}
    var videoAndOptions by remember{mutableStateOf(randomSelector.getNextVideoAndOptions())}
    var timerFillPortion by remember{mutableStateOf(1.0f)}
    var screenMode by remember {mutableStateOf(ScreenMode.PLAY)}
    var heartsLeft by remember {mutableStateOf(5)}
    var score by remember {mutableStateOf(0)}
    var thisQuestionPoints by remember {mutableStateOf(0)}

    var currentTimer: CountDownTimer? by remember{mutableStateOf(null)}
    if(currentTimer == null && screenMode == ScreenMode.PLAY){
        currentTimer = StartTimer(changeFillPortion =   {it ->
                                                            timerFillPortion = it
                                                            thisQuestionPoints = (100 * it).toInt()
                                                        },
                    onTimeout = {screenMode = ScreenMode.TIME_OUT
                                 heartsLeft--
                                }
        )
    }


    if(heartsLeft < 0) screenMode = ScreenMode.GAME_OVER
    if(videoAndOptions == null) screenMode = ScreenMode.ALL_FINISHED


    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.background(
            if(screenMode == ScreenMode.PLAY) SignaBackground else SignaDark
        )
    ) {
        FullHeader(navController, score)
        if(screenMode == ScreenMode.PLAY){
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimeBar(timerFillPortion)
                Spacer(Modifier.width(10.dp))
                HeartsLeft(heartsLeft)
            }
            Spacer(Modifier.height(10.dp))
        }

        when (screenMode) {
            ScreenMode.PLAY -> {
                Text(
                    text = stringResource(R.string.quiz_prompt),
                    fontFamily = Poppins,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(Modifier.height(4.dp))

                Column(Modifier.padding(horizontal = 40.dp, vertical = 0.dp)) {
                    if(videoAndOptions != null) {
                        ImageOrVideoPlayer(context, videoAndOptions!!.videoPath, exoPlayerManager)
                        Spacer(Modifier.height(25.dp))
                        FourOptions(videoAndOptions!!.options, videoAndOptions!!.correctIndex,
                            onClickCorrect = {
                                screenMode = ScreenMode.CORRECT
                                score += thisQuestionPoints
                                thisQuestionPoints = 0
                                currentTimer?.cancel()
                                currentTimer = null
                            },
                            onClickIncorrect = {
                                screenMode = ScreenMode.INCORRECT
                                heartsLeft--
                                currentTimer?.cancel()
                                currentTimer = null
                            }
                        )
                    }
                }
            }
            ScreenMode.TIME_OUT -> {
                AfterQuestionUI(stringResource(R.string.timeout), true, false) {
                    videoAndOptions = randomSelector.getNextVideoAndOptions()
                    currentTimer?.cancel()
                    currentTimer = null
                    screenMode = ScreenMode.PLAY
                }
            }
            ScreenMode.CORRECT -> {
                AfterQuestionUI(stringResource(R.string.correcto), false, false) {
                    videoAndOptions = randomSelector.getNextVideoAndOptions()
                    currentTimer?.cancel()
                    currentTimer = null
                    screenMode = ScreenMode.PLAY
                }
            }
            ScreenMode.INCORRECT -> {
                AfterQuestionUI(stringResource(R.string.incorrecto), true, false) {
                    videoAndOptions = randomSelector.getNextVideoAndOptions()
                    currentTimer?.cancel()
                    currentTimer = null
                    screenMode = ScreenMode.PLAY
                }
            }
            ScreenMode.GAME_OVER -> {
                AfterQuestionUI(stringResource(R.string.game_over), true, true) {
                    coroutineScope.launch(Dispatchers.IO) {
                        HTTPUserManager.postScore(score)
                        coroutineScope.launch(Dispatchers.Main){
                            navController.popBackStack()
                        }
                    }
                }
            }
            ScreenMode.ALL_FINISHED -> {
                AfterQuestionUI(stringResource(R.string.all_finished), false, true) {
                    coroutineScope.launch(Dispatchers.IO) {
                        HTTPUserManager.postScore(score)
                        HTTPUserManager.postCategoryProgress(categories)
                        coroutineScope.launch(Dispatchers.Main){
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullHeader(navController: NavController, points: Int){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .requiredHeight(120.dp)
            .fillMaxWidth()
            .background(SignaGreen)
    ) {
        Box() {
            HeaderTitle(stringResource(R.string.quiz))
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(10.dp))
                BackButton(navController)
            }
        }
        Spacer(Modifier.height(8.dp))
        ScoreBanner(score = points)
    }
}

@Composable
private fun ScoreBanner(profilePic: Painter = painterResource(R.drawable.guest_user_profile_pic),
                        score: Int = 0
){
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(SignaLight),
        modifier = Modifier
            .fillMaxWidth(0.5f)
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
            text = stringResource(R.string.puntos),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun TimeBar(fillPortion: Float){
    LinearProgressIndicator(
        backgroundColor = SignaDarkVeryTransparent,
        color = SignaRed,
        progress = fillPortion,
        modifier = Modifier
            .height(20.dp)
            .clip(RoundedCornerShape(100))
            .border(1.dp, SignaDark, RoundedCornerShape(100))
    )
}

@Composable
private fun HeartsLeft(heartsLeft: Int){
    Column(){
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            Column(){
                Image(painterResource(R.drawable.heart), null,
                    modifier = Modifier.size(30.dp))
                Spacer(Modifier.height(8.dp))
            }
            Text("x$heartsLeft", style = MaterialTheme.typography.body1, color = SignaRed)
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
        .border(width = 3.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
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
        .border(width = 3.dp, color = SignaDark, shape = RoundedCornerShape(16.dp))
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
private fun FourOptions(options: Vector<String>, correctIndex: Int,
                        onClickCorrect: () -> Unit, onClickIncorrect: () -> Unit
){
    Column(horizontalAlignment = CenterHorizontally, modifier = Modifier.fillMaxWidth()){
        Row(horizontalArrangement = Arrangement.Center){
            OptionButton(options[0], 0 == correctIndex, onClickCorrect, onClickIncorrect)
            Spacer(Modifier.width(16.dp))
            OptionButton(options[1], 1 == correctIndex, onClickCorrect, onClickIncorrect)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.Center){
            OptionButton(options[2], 2 == correctIndex, onClickCorrect, onClickIncorrect)
            Spacer(Modifier.width(16.dp))
            OptionButton(options[3], 3 == correctIndex, onClickCorrect, onClickIncorrect)
        }
    }
}

@Composable
private fun OptionButton(text: String, correct: Boolean,
                         onClickCorrect: () -> Unit, onClickIncorrect: () -> Unit
){
    Button(
        onClick = if(correct) onClickCorrect else onClickIncorrect,
        colors = ButtonDefaults.buttonColors(backgroundColor = SignaLight),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SignaDark)
    ) {
        Text(text = text, style = MaterialTheme.typography.body1, fontSize = 16.sp)
    }
}

@Composable
private fun AfterQuestionUI(message: String, msgInRed: Boolean, mentionSaving: Boolean,
                            onContinue: () -> Unit){
    val infiniteColorTransition = rememberInfiniteTransition()
    val color by infiniteColorTransition.animateColor(
        initialValue = SignaDark,
        targetValue = SignaLight,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxSize(),
        border = BorderStroke(0.dp, Color.Transparent),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
    ) {
        Column(horizontalAlignment = CenterHorizontally){
            Text(text = message,
                fontFamily = Poppins,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = if(msgInRed) SignaRed else SignaGreen
            )
            Text(text = if(mentionSaving) {
                stringResource(R.string.toca_para_continuar_y_guardar)
            } else {stringResource(R.string.toca_para_continuar)},
                style = MaterialTheme.typography.body1,
                color = color
            )
        }
    }
}

private fun StartTimer(changeFillPortion: (Float) -> Unit, onTimeout: () -> Unit): CountDownTimer {
    val BONUS: Long = 2000
    val SECONDS_TO_TIME: Long = 13000
    val timer = object: CountDownTimer(SECONDS_TO_TIME + BONUS, 15) {
        override fun onTick(millisUntilFinished: Long) {
            val portion = minOf(1.0f, millisUntilFinished.toFloat() / SECONDS_TO_TIME)
            changeFillPortion(portion)
        }

        override fun onFinish() {
            onTimeout()
        }
    }
    timer.start()
    return timer
}