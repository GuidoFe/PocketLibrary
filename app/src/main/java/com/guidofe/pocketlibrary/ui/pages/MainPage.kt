package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.MainBottomBar
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewModel: IMainActivityVM = hiltViewModel<MainActivityVM>()) {
    val navController = rememberNavController()
    val scaffoldState = viewModel.scaffoldState
    val refreshFabBlacklist = listOf("library_page", "wishlist_page")
    LaunchedEffect(key1 = true) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!refreshFabBlacklist.contains(destination.route))
                scaffoldState.fab = {}
            scaffoldState.actions = {}
        }
    }
    Scaffold(
        snackbarHost = {
            // reuse default SnackbarHost to have default animation and timing handling
            SnackbarHost(viewModel.snackbarHostState) { data ->
                // custom snackbar with the custom action button color and border
                val custom = (data.visuals as? CustomSnackbarVisuals)
                val isError = custom?.isError ?: false
                val buttonColor = if (isError) {
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.inversePrimary
                    )
                }

                Snackbar(
                    modifier = Modifier.padding(5.dp),
                    action = {
                        data.visuals.actionLabel?.let {
                            TextButton(
                                onClick = { if (isError) data.dismiss() else data.performAction() },
                                colors = buttonColor
                            ) { Text(it) }
                        }
                    },
                    dismissAction = {
                        if (custom?.isError == true) {
                            IconButton(
                                onClick = { data.dismiss() }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24px),
                                    contentDescription = stringResource(R.string.close),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    containerColor = if (custom?.isError == true) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        SnackbarDefaults.color
                    },
                    contentColor = if (custom?.isError == true)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        SnackbarDefaults.contentColor
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        bottomBar = { MainBottomBar(navController) },
        topBar = {
            if (!scaffoldState.hiddenBar) {
                TopAppBar(
                    title = {
                        Text(scaffoldState.title, style = MaterialTheme.typography.titleLarge)
                    },
                    actions = scaffoldState.actions,
                    navigationIcon = scaffoldState.navigationIcon,
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                )
            }
        },
        floatingActionButton = {
            // TODO: Animate fab changing
            viewModel.scaffoldState.fab()
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
        }
    }
}

@Composable
@Preview
private fun MainPagePreview() {
    PocketLibraryTheme {
        MainPage()
    }
}
