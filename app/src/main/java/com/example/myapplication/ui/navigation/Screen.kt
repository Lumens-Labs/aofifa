package com.example.myapplication.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Global : Screen("global", "Global")
    object Asado : Screen("asado", "Asado")
    object H2H : Screen("h2h", "H2H")
}
