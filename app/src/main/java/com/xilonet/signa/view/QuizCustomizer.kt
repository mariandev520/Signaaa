package com.xilonet.signa.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xilonet.signa.R
import com.xilonet.signa.controller.Screen


@Composable
fun EarIcons(navController: NavController) {

    Image(
        painter = painterResource(id = R.drawable.back), // Reemplaza con el ID de tu imagen de fondo
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(1300.dp), // Ajusta la altura según tus necesidade
        contentScale = ContentScale.Crop
    )

    Spacer(Modifier.padding(20.dp))


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB2CECD)) // Color pastel claro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Alinea los iconos verticalmente en el centro
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de estrella con navegación
            ClickableText(
                text = AnnotatedString(""),
                onClick = { offset ->
                    // Navegar a la ubicación deseada cuando se hace clic en el icono de la estrella
                    navController.navigate(Screen.Inicio.route)
                }
            )
            Spacer(Modifier.padding(15.dp))
            Image(
                painter = painterResource(id = R.drawable.personas), // Reemplaza con el ID de tu imagen de fondo
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Inicio.route)
                }
            )
            Spacer(Modifier.padding(35.dp))


            // Otro icono (sin navegación)
            Image(
                painter = painterResource(id = R.drawable.discapacidad),
                contentDescription = null,
                // Ajusta la altura según tus necesidade
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                )

            {
                ClickableText(
                    text = AnnotatedString(" "),
                    onClick = { offset ->
                        // Navegar a la ubicación deseada cuando se hace clic en el icono de la estrella
                        navController.navigate(Screen.Quiz.route)

                    }
                )
            }
        }

    }
}

@Composable
fun EarIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    contentDescription: String
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = color,
        modifier = Modifier
            .size(120.dp)
            .background(color = Color.White, shape = CircleShape)
            .padding(8.dp)
    )
}



