package com.guidofe.pocketlibrary.ui.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.BottomBarDestination
import com.guidofe.pocketlibrary.ui.MainBottomBar
import com.guidofe.pocketlibrary.ui.pages.destinations.Destination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.MainActivityViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigateTo
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@ExperimentalMaterialApi
@Composable
fun MainPage() {

    val navController = rememberNavController();
    Scaffold(
        bottomBar = { MainBottomBar(navController) },
        floatingActionButton = {
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            DestinationsNavHost(navController = navController, navGraph = NavGraphs.root)
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview
fun MainPagePreview() {
    PocketLibraryTheme() {

        MainPage()
    }
}
