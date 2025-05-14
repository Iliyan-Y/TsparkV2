package com.example.tspark

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tspark.ui.components.HamburgerMenu
import com.example.tspark.ui.components.TopNavBar
import com.example.tspark.ui.navigation.AppNavHost
import com.example.tspark.ui.navigation.AppScreen
import kotlinx.coroutines.launch


@Composable
fun TSparkApp(
    navController: NavHostController = rememberNavController()
) {
    val focusManager = LocalFocusManager.current
    // Get current back stack entry
    val backStackEntry = navController.currentBackStackEntryAsState().value
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Start.name
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    fun toggleDrawer() {
        if (drawerState.isClosed) {
            drawerScope.launch {
                drawerState.open()
            }
        } else {
            drawerScope.launch {
                drawerState.close()
            }
        }
    }

    HamburgerMenu(
        drawerState = drawerState,
        drawerScope,
        currentScreen,
        navController,
        {
            Scaffold(
                topBar = {
                    TopNavBar(
                        currentScreen = currentScreen,
                        canNavigateBack = navController.previousBackStackEntry != null && currentScreen == AppScreen.Settings,
                        navController,
                        toggleDrawer = { toggleDrawer() }
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }) { innerPadding ->

                AppNavHost(innerPadding, navController)
            }
        }
    )
}