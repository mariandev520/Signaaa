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
import androidx.compose.material.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .height(1300.dp), // Ajusta la altura según tus necesidades
        contentScale = ContentScale.Crop
    )

    Spacer(Modifier.padding(20.dp))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2F1)) // Color pastel claro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Alinea los iconos verticalmente en el centro
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono "Aprender Seña" con navegación
            ClickableText(
                text = AnnotatedString("Servicios"),
                onClick = { offset ->
                    // Navegar a la ubicación deseada cuando se hace clic en el icono
                    navController.navigate(Screen.Inicio.route)
                },
                style = TextStyle(
                    fontSize = 40.sp, // Tamaño de fuente más grande
                    fontWeight = FontWeight.Bold, // Texto en negrita
                    color = Color(0xFF007AFF) // Color similar a iOS
                )
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
            Spacer(Modifier.padding(5.dp))
            Text(
                text = "Buscar Seña ",
                style = TextStyle(
                    fontSize = 19.sp, // Tamaño de fuente más grande
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF007AFF) // Color similar a iOS
                )
            )

            Spacer(Modifier.padding(30.dp))

            // Icono "Traductor de Señas" (sin navegación)
            Image(
                painter = painterResource(id = R.drawable.discapacidad),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Quiz.route)
                }
            )
            Spacer(Modifier.padding(5.dp))
            Text(
                text = "Traductor de Señas",
                style = TextStyle(
                    fontSize = 19.sp, // Tamaño de fuente más grande
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF007AFF) // Color similar a iOS
                )
            )

            ClickableText(
                text = AnnotatedString(" "),
                onClick = { offset ->
                    // Navegar a la ubicación deseada cuando se hace clic en el icono
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }
    }
}

