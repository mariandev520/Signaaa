package com.xilonet.signa.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.xilonet.signa.controller.SetupNavGraph
import com.xilonet.signa.view.theme.SignaBackground
import com.xilonet.signa.view.theme.SignaGreen
import com.xilonet.signa.view.theme.SignaTheme

/* TODO:
    Para mejorar el rendimiento, se podría intentar usar un solo VideoFilesManager para toda la app,
    en lugar de crear uno en cada pantalla que lo requiere. Recordemos que el VideoFilesManager
    tiene un constructor de inicialización relativamente pesado.
 */
class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SignaBackground
                ) {
                    navController = rememberNavController()
                    SetupNavGraph(navController = navController, context = applicationContext)
                }
            }
        }
    }

}