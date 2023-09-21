package com.xilonet.signa.controller

const val QUIZ_CATEGORIES_KEY = "quiz_categories"

sealed class Screen(val route: String) {
    object Login: Screen(route = "login_screen")
    object Inicio: Screen(route = "inicio_screen")
    object Diccionario: Screen(route = "diccionario_screen")
    object QuizCustomizer: Screen(route = "quiz_customizer_screen")
    object Quiz: Screen(route = "quiz_screen/{$QUIZ_CATEGORIES_KEY}") {
        fun passPipeSeparatedCategories(categories: String): String{
            return this.route.replace(oldValue = "{$QUIZ_CATEGORIES_KEY}", newValue = categories)
        }
    }
}