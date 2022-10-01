package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.ui.MainBottomBar
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.IMainActivityViewModel
import com.guidofe.pocketlibrary.viewmodels.MainActivityViewModel
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewModel: IMainActivityViewModel = hiltViewModel<MainActivityViewModel>()) {
    val navController = rememberNavController()
    val appBarState by viewModel.appBarState.collectAsState()
    Scaffold(
        bottomBar = { MainBottomBar(navController) },
        topBar = {
            if (appBarState != null) {
                TopAppBar(
                    title = {Text(appBarState!!.title)},
                    actions = appBarState!!.actions,
                    navigationIcon = appBarState!!.navigationIcon
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
        }
    }
}

@Composable
@Preview
fun MainPagePreview() {
    PocketLibraryTheme() {

        MainPage()
    }
}
