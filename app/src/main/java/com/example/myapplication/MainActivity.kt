package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.AoBlack
import com.example.myapplication.ui.theme.AoOrange
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AsadoViewModel
import com.example.myapplication.ui.viewmodel.MainViewModel
import com.example.myapplication.ui.viewmodel.ViewModelFactory
import com.example.myapplication.update.GithubRelease
import com.example.myapplication.update.UpdateDialog
import com.example.myapplication.BuildConfig

class MainActivity : ComponentActivity() {
    
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as AoApplication).repository)
    }

    private val asadoViewModel: AsadoViewModel by viewModels {
        ViewModelFactory((application as AoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as AoApplication

        setContent {
            MyApplicationTheme {
                var releaseInfo by remember { mutableStateOf<GithubRelease?>(null) }

                LaunchedEffect(Unit) {
                    // Reemplazar con tus datos reales de GitHub
                    releaseInfo = app.updateService.checkForUpdates(
                        "Lumens-Labs", 
                        "aofifa", 
                        BuildConfig.VERSION_NAME
                    )
                }

                releaseInfo?.let { release ->
                    UpdateDialog(
                        release = release,
                        onConfirm = {
                            val apkAsset = release.assets.firstOrNull { it.name.endsWith(".apk") }
                            apkAsset?.let { app.updateManager.downloadAndInstall(it.downloadUrl, release.tagName) }
                            releaseInfo = null
                        },
                        onDismiss = { releaseInfo = null }
                    )
                }

                MainScreen(mainViewModel, asadoViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel, asadoViewModel: AsadoViewModel) {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Asado,
        Screen.Stats,
        Screen.Players,
        Screen.Sync,
        Screen.Help
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on top level screens
            val showBottomBar = screens.any { it.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = AoBlack,
                    contentColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    screens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { 
                                val icon = when(screen) {
                                    Screen.Asado -> Icons.Default.DateRange
                                    Screen.Stats -> Icons.Default.Info
                                    Screen.Players -> Icons.Default.Person
                                    Screen.Sync -> Icons.Default.Refresh
                                    Screen.Help -> Icons.AutoMirrored.Filled.Help
                                    Screen.MatchDetails -> Icons.AutoMirrored.Filled.List
                                    Screen.ActiveAsado -> Icons.Default.Star
                                }
                                Icon(
                                    icon, 
                                    contentDescription = null,
                                    tint = if (selected) Color.White else Color.Gray
                                )
                            },
                            label = { 
                                Text(
                                    screen.title, 
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) Color.White else Color.Gray
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = AoOrange.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Asado.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Asado.route) { 
                    AsadoScreen(
                        mainViewModel = mainViewModel, 
                        asadoViewModel = asadoViewModel,
                        onAsadoClick = { asadoId ->
                            navController.navigate(Screen.MatchDetails.createRoute(asadoId))
                        },
                        onActiveAsadoClick = { asadoId ->
                            navController.navigate(Screen.ActiveAsado.createRoute(asadoId))
                        }
                    ) 
                }
                composable(Screen.Stats.route) { StatsScreen(mainViewModel) }
                composable(Screen.Players.route) { PlayersScreen(mainViewModel) }
                composable(Screen.Sync.route) { SyncScreen(mainViewModel) }
                composable(Screen.Help.route) { HelpScreen() }
                
                composable(
                    route = Screen.MatchDetails.route,
                    arguments = listOf(navArgument("asadoId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val asadoId = backStackEntry.arguments?.getString("asadoId") ?: ""
                    MatchDetailsScreen(asadoId = asadoId, viewModel = mainViewModel)
                }

                composable(
                    route = Screen.ActiveAsado.route,
                    arguments = listOf(navArgument("asadoId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val asadoId = backStackEntry.arguments?.getString("asadoId") ?: ""
                    ActiveAsadoScreen(
                        asadoId = asadoId,
                        mainViewModel = mainViewModel,
                        asadoViewModel = asadoViewModel,
                        onNavigateBack = { 
                            navController.popBackStack(Screen.Asado.route, false)
                        }
                    )
                }
            }

            // Version label
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                fontSize = 10.sp,
                color = Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 4.dp)
            )
        }
    }
}
