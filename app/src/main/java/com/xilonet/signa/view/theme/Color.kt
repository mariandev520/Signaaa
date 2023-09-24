package com.xilonet.signa.view.theme

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.unit.dp

// Colores originales
var SignaGreen = Color(0x9CCAE9EC)
var SignaBackground = Color(0xFF4DD0E1)
var SignaDark = Color(0xFF44405D)
var SignaDarkSemiTransparent = Color(0xFF6A1B9A)
var SignaDarkVeryTransparent = Color(0xFFBF360C)
var SignaLight = Color(0xFFE0F7FA)
var SignaRed = Color(0xFFFF4D4A)
var SignaYellow = Color(0xFF2196F3)
var SignaDarka = Color(0xFF9FA8DA)
var SignaDarkaa = Color(0xFF000000)



@Composable
fun GradientColorScreen(color: Color) {
    val colors = listOf(
        color,
        Color.White // Cambia esto al color de fondo que desees
    )

    val gradientBrush = Brush.verticalGradient(colors)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
}

