package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screens.AsadoScreen
import com.example.myapplication.ui.screens.GlobalScreen
import com.example.myapplication.ui.screens.H2HScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AsadoViewModel
import com.example.myapplication.ui.viewmodel.MainViewModel
import com.example.myapplication.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as AoApplication).repository)
    }

    private val asadoViewModel: AsadoViewModel by viewModels {
        ViewModelFactory((application as AoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen(mainViewModel, asadoViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel, asadoViewModel: AsadoViewModel) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Global, Screen.Asado, Screen.H2H)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            when(screen) {
                                Screen.Global -> Icon(Icons.Default.Home, contentDescription = null)
                                Screen.Asado -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                                Screen.H2H -> Icon(Icons.Default.Person, contentDescription = null)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Global.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Global.route) { GlobalScreen(mainViewModel) }
            composable(Screen.Asado.route) { AsadoScreen(mainViewModel, asadoViewModel) }
            composable(Screen.H2H.route) { H2HScreen(mainViewModel) }
        }
    }
}
