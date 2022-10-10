package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.ui.MainBottomBar
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewModel: IMainActivityVM = hiltViewModel<MainActivityVM>()) {
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
        },
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
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
