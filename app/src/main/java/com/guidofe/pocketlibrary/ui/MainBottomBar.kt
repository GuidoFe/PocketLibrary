package com.guidofe.pocketlibrary.ui

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.guidofe.pocketlibrary.ui.pages.appDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.Destination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun MainBottomBar(navController: NavController) {
    val currentDestination: Destination? =
        navController.currentBackStackEntryAsState().value?.appDestination()
    NavigationBar {
        BottomBarDestination.values().forEach { screen ->
            NavigationBarItem(
                selected = currentDestination == screen.direction,
                icon = { Icon(painterResource(screen.iconId), contentDescription = null) },
                onClick = {
                    try {
                        if (currentDestination != screen.direction) {
                            navController.navigate(screen.direction, fun NavOptionsBuilder.() {
                                launchSingleTop = true
                            })
                        }
                    } catch (e: IllegalStateException) {
                        Log.e("test", "Illegal state exception at bottom menu")
                    }
                }
            )
        }
    }
}