package com.guidofe.pocketlibrary.ui

import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.guidofe.pocketlibrary.ui.pages.appDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.Destination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun MainNavigationRail(navController: NavController) {
    val currentDestination: Destination? =
        navController.currentBackStackEntryAsState().value?.appDestination()
    val scrollState = rememberScrollState()
    NavigationRail() {
        BottomBarDestination.values().forEach { screen ->
            NavigationRailItem(
                selected = currentDestination?.route?.root == screen.direction.route.root,
                icon = { Icon(painterResource(screen.iconId), contentDescription = null) },
                label = { Text(stringResource(screen.labelId)) },
                onClick = {
                    try {
                        if (currentDestination?.route?.root != screen.direction.route.root) {
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