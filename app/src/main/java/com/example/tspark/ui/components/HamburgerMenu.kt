package com.example.tspark.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tspark.R
import com.example.tspark.ui.navigation.AppScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HamburgerMenu(
    drawerState: DrawerState,
    menuScope: CoroutineScope,
    currentScreen: AppScreen,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Drawer Title",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                menuScope.launch {
                                    drawerState.close()
                                }
                            },
                        style = MaterialTheme.typography.titleLarge,

                        )
                    HorizontalDivider()

                    Text(
                        "Calculators",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Charge") },
                        selected = currentScreen == AppScreen.Start,
                        onClick = {
                            if (currentScreen != AppScreen.Start) {
                                navController.navigate(AppScreen.Start.name)
                            }
                            menuScope.launch {
                                drawerState.close()
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.calcDistance).split(" ")[0]) },
                        selected = currentScreen == AppScreen.CalcDistance,
                        onClick = {
                            if (currentScreen != AppScreen.CalcDistance) {
                                navController.navigate(AppScreen.CalcDistance.name)
                            }
                            menuScope.launch {
                                drawerState.close()
                            }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Section 2",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = currentScreen == AppScreen.Settings,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        badge = { Text("battery/range etc.") }, // Placeholder
                        onClick = {
                            if (currentScreen != AppScreen.Settings) {
                                navController.navigate(AppScreen.Settings.name)
                            }
                            menuScope.launch {
                                drawerState.close()
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Help and feedback") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                        onClick = { /* Handle click */ },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        content()
    }
}