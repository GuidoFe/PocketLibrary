package com.guidofe.pocketlibrary.ui

import android.util.Log
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.guidofe.pocketlibrary.ui.pages.destinations.Destination
import com.guidofe.pocketlibrary.ui.pages.navDestination
import com.ramcosta.composedestinations.navigation.navigateTo
import java.lang.IllegalStateException

@ExperimentalMaterialApi
@Composable
fun MainBottomBar(navController: NavController){
    val currentDestination: Destination? = navController.currentBackStackEntryAsState().value?.navDestination;
    BottomNavigation {
        BottomBarDestination.values().forEach { screen ->
            BottomNavigationItem(selected = currentDestination == screen.direction,
                icon = { Icon(painterResource(screen.iconId), contentDescription = null) },
                onClick = {
                    try {
                        navController.navigateTo(screen.direction) {
                            launchSingleTop = true
                        }
                    } catch(e: IllegalStateException){
                        Log.e("test", "Illegal state exception at bottom menu")
                    }
                })
        }
    }
}