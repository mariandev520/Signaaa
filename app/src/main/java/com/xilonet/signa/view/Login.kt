package com.xilonet.signa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xilonet.signa.R
import com.xilonet.signa.controller.Screen
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.view.theme.SignaDark
import com.xilonet.signa.view.theme.SignaGreen
import com.xilonet.signa.view.theme.SignaLight
import com.xilonet.signa.view.theme.SignaRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
@Composable
fun LoginUI(navController: NavController){
    Surface(Modifier.fillMaxSize(), color = SignaGreen){
        // From top to bottom:
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.fillMaxHeight(0.15f))
            AppNameBanner()
        }

        // From bottom to top:
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginFieldsAndButton() { navController.navigate(Screen.Inicio.route) }
            Spacer(Modifier.fillMaxHeight(0.3f))
        }
    }

}

@Composable
private fun AppNameBanner(){
    Image(
        painter = painterResource(R.drawable.login_screen_logo),
        contentDescription = stringResource(R.string.login_screen_logo),
        colorFilter = ColorFilter.tint(color = SignaDark),
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth(0.7f)
    )
}

private lateinit var coroutineScope : CoroutineScope

@Composable
private fun LoginFieldsAndButton(goToInicio: () -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var allowEdit by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    var showPassword by remember { mutableStateOf(false) }
    var failedLogin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (failedLogin) {
        Text(
            text = stringResource(R.string.failed_login),
            style = MaterialTheme.typography.body2,
            color = SignaRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }

    // Email
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        label = {
            Text(
                text = stringResource(R.string.correo),
                fontSize = 18.sp,
                color = Color.Gray
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = SignaDark,
            backgroundColor = SignaLight,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray
        ),
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        enabled = allowEdit
    )

    Spacer(Modifier.height(8.dp))

    // Password
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        label = {
            Text(
                text = stringResource(R.string.contrasena),
                fontSize = 18.sp,
                color = Color.Gray
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = SignaDark,
            backgroundColor = SignaLight,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray
        ),
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            val image = if (showPassword)
                painterResource(R.drawable.ic_baseline_visibility_24)
            else painterResource(R.drawable.ic_baseline_visibility_off_24)

            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(image, null)
            }
        },
        enabled = allowEdit
    )

    // Log-in button
    Spacer(Modifier.height(20.dp))
    LoginScreenGenericButton(
        text = stringResource(R.string.login),
        enabled = allowEdit,
        onClick = {
            allowEdit = false
            failedLogin = false
            coroutineScope.launch(Dispatchers.IO) {
                val userInfo = HTTPUserManager.tryLogIn(email.text, password.text)
                coroutineScope.launch(Dispatchers.Main) {
                    if (userInfo == null) {
                        allowEdit = true
                        failedLogin = true
                    } else {
                        goToInicio()
                    }
                }
            }
        },
    )

    // Continue as guest button
    Spacer(Modifier.height(20.dp))
    LoginScreenGenericButton(
        text = stringResource(R.string.continue_as_guest),
        enabled = allowEdit,
        onClick = {
            HTTPUserManager.nullUserInfo()
            goToInicio()
        },
        guest = true,
        fontSize = 16.sp,
    )
}


@Composable
private fun LoginScreenGenericButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    guest: Boolean = false,
    fontSize: TextUnit = 20.sp
) {
    val buttonColors = if (guest) {
        ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        )
    } else {
        val defaultColor = SignaLight
        val hoverColor = SignaDark

        val backgroundColor = if (enabled) defaultColor else hoverColor
        val contentColor = if (enabled) hoverColor else defaultColor

        ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = backgroundColor,
            contentColor = contentColor
        )
    }

    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(50.dp)
            .border(
                width = 2.dp,
                color = SignaDark,
                shape = RoundedCornerShape(50)
            )
            .padding(0.dp),
        shape = RoundedCornerShape(50),
        border = if (guest && enabled) BorderStroke(2.dp, SignaDark) else null,
        enabled = enabled
    ) {
        if (!enabled && !guest) {
            CircularProgressIndicator(modifier = Modifier.height(35.dp))
        } else if (!(enabled && guest)) {
            Text(text = text, style = MaterialTheme.typography.body1, fontSize = fontSize)
        }
    }
}
