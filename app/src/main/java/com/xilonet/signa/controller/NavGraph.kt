package com.xilonet.signa.controller

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xilonet.signa.view.*

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    context: Context
){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginUI(navController)
        }
        composable(
            route = Screen.Inicio.route
        ) {
            InicioUI(navController)
        }
        composable(
            route = Screen.Diccionario.route
        ) {
            DiccionarioUI(context, navController, onCloseClick = {})
        }
        composable(
            route = Screen.QuizCustomizer.route
        ) {
            QuizCustomizerUI(context, navController)
        }
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument(QUIZ_CATEGORIES_KEY){
                type = NavType.StringType
            })
        ) {
            val videoCategories = it.arguments?.getString(QUIZ_CATEGORIES_KEY)?.split("|")
            if (videoCategories != null) {
                videoCategories.forEach{
                    videoCat -> Log.d("VIDEOCAT", videoCat)
                }
                QuizUI(context, navController, videoCategories)
            }
        }
    }
}