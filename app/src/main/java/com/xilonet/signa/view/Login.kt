package com.xilonet.signa.view


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xilonet.signa.R
import com.xilonet.signa.controller.Screen
import com.xilonet.signa.model.HTTPUserManager
import com.xilonet.signa.view.theme.SignaDark
import com.xilonet.signa.view.theme.SignaLight
import com.xilonet.signa.view.theme.SignaRed
import com.xilonet.signa.view.theme.SignaYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginUI(navController: NavController) {
/*
    Image(
        painter = painterResource(id = R.drawable.backa), // Reemplaza con el ID de tu imagen de fondo
        contentDescription = null,
        modifier = Modifier
            .background(Color(0xFFE0E0E0))
            .fillMaxWidth()
            .height(1500.dp), // Ajusta la altura según tus necesidade
        contentScale = ContentScale.Crop
    )

*/

    val gradientColors = listOf(

    SignaYellow,
    Color(0xFFE0E0E0)// Cambia esto al color de fondo que desees
    )


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Agrega un fondo con el gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(gradientColors)),
        )
        // Agrega la imagen de fondo


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.fillMaxHeight(0.25f))


            LoginFieldsAndButton() { navController.navigate(Screen.QuizCustomizer.route) }
            Spacer(Modifier.fillMaxHeight(0.3f))
        }


    }

}

@Composable
fun AnimatedTextWithFade() {
    var isTextExpanded by remember { mutableStateOf(false) }

    val text = if (isTextExpanded) {
        "Aprende a comunicarte Simple"
    } else {
        "Aprende a comunicarte"
    }

    val fontSize = if (isTextExpanded) 30.sp else 20.sp // Cambia el tamaño de fuente en función del estado

    val alpha by animateFloatAsState(if (isTextExpanded) 1f else 0.5f)

    Text(
        text = text,
        fontSize = fontSize,
        color = Color.White.copy(alpha = alpha),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .padding(vertical = 16.dp) // Agrega espacio vertical si es necesario
            .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable {
                isTextExpanded = !isTextExpanded // Cambia el estado en clic
            }
            .animateContentSize() // Agrega animación al cambio de tamaño
            .padding(8.dp), // Añade un padding adicional para mejorar el clic
        textAlign = TextAlign.Center, // Alinea el texto al centro
        fontWeight = FontWeight.Bold, // Aplica negritas si lo deseas
        fontFamily = FontFamily.SansSerif // Cambia la fuente si es necesario
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
                text = stringResource(R.string.email),
                fontSize = 18.sp,
                color = Color.Black
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.Transparent, // Hacer el fondo transparente
            focusedBorderColor = Color.Cyan,
            unfocusedBorderColor = Color.Cyan,
            disabledBorderColor = Color.Cyan
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
                text = stringResource(R.string.prompt_password),
                fontSize = 18.sp,
                color = Color.Black
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = SignaDark,
            backgroundColor = Color.Transparent, // Hacer el fondo transparente
            focusedBorderColor = Color.Cyan,
            unfocusedBorderColor = Color.Cyan,
            disabledBorderColor = Color.Cyan
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
    Spacer(Modifier.height(30.dp))
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
        transparency = 0.2f, // Hace el botón un 50% transparente
        fontSize = 24.sp, // Ajusta el tamaño del texto a 24sp
        buttonSizeModifier = Modifier.fillMaxWidth(0.5f) // Reduce el tamaño del botón al 50%

    )


        Text(
            text = "Test App", // Cambia el texto aquí
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = if (allowEdit) Color.White else Color.White, // Cambia el color según el estado de habilitación
            modifier = Modifier
                .padding(16.dp)
                .clickable(enabled = allowEdit) {
                    if (allowEdit) {
                        HTTPUserManager.nullUserInfo()
                        goToInicio()

                    }

                }
        )






    Spacer(Modifier.height(10.dp))
    // Continue as guest button

    AnimatedTextWithFade();
    Spacer(Modifier.height(110.dp))


}

private fun getRandomDescription(): String {
    // Implementa aquí la lógica para obtener un texto aleatorio
    return "Texto descriptivo aleatorio"
}

@Composable
private fun LoginScreenGenericButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    guest: Boolean = false,
    fontSize: TextUnit = 40.sp,
    transparency: Float = 0.7f, // Valor para la transparencia (0.0f a 1.0f)
    buttonSizeModifier: Modifier = Modifier
        .fillMaxWidth(0.7f)
        .padding(vertical = 20.dp),

        // Modificador de tamaño del botón
)
{
    val buttonText = if (guest) "Guest" else text // Cambiar el texto si es un botón de invitado



    val buttonColors = if (guest) {
        ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent, // Fondo blanco para el botón de invitado
            contentColor = Color.White // Texto negro para el botón de invitado
        )
    } else {
        val defaultColor = SignaLight.copy(alpha = transparency) // Ajusta la transparencia del fondo
        val hoverColor = SignaDark.copy(alpha = transparency) // Ajusta la transparencia del fondo

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
        modifier = buttonSizeModifier, // Aplica el modificador de tamaño
        shape = RoundedCornerShape(50),
        border = if (guest && enabled) BorderStroke(2.dp, Color.Cyan) else null,
        enabled = enabled
    ) {
        if (!enabled && !guest) {
            CircularProgressIndicator(modifier = Modifier.height(45.dp))
        } else if (!(enabled && guest)) {
            Text(
                text = "Login", // Utiliza el texto modificado
                style = MaterialTheme.typography.body1.copy(
                    color = if (guest) Color.Black else MaterialTheme.typography.body1.color
                ),
                fontSize = fontSize
            )


        }

    }


    }

