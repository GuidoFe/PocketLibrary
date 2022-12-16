package com.guidofe.pocketlibrary.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.bottomsheet.ModalBottomSheetLayout
import com.guidofe.pocketlibrary.ui.pages.NavGraphs
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import com.ramcosta.composedestinations.DestinationsNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    viewModel: IMainActivityVM = hiltViewModel<MainActivityVM>(),
) {
    val navController = rememberNavController()
    val scaffoldState = viewModel.scaffoldState
    val windowInfo = rememberWindowInfo()
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            scaffoldState.fab = {}
            scaffoldState.actions = {}
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier.heightIn(min = 1.dp)
            ) {
                scaffoldState.bottomSheetContent(this)
            }
        },
        sheetState = scaffoldState.bottomSheetState,
        sheetShape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize
        )
    ) {
        Scaffold(
            topBar = {
                if (!scaffoldState.hiddenBar) {
                    TopAppBar(
                        title = scaffoldState.title,
                        actions = scaffoldState.actions,
                        navigationIcon = scaffoldState.navigationIcon,
                        scrollBehavior = viewModel.scaffoldState.scrollBehavior
                    )
                }
            },
            bottomBar = {
                if (windowInfo.isBottomAppBarLayout()) MainBottomBar(navController)
            },
            floatingActionButton = {
                // TODO: Animate fab changing
                viewModel.scaffoldState.fab()
            },
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
                        modifier = Modifier.padding(8.dp),
                        action = {
                            data.visuals.actionLabel?.let {
                                TextButton(
                                    onClick = {
                                        if (isError)
                                            data.dismiss()
                                        else
                                            data.performAction()
                                    },
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
                            MaterialTheme.colorScheme.inverseSurface
                        },
                        contentColor = if (custom?.isError == true)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.inverseOnSurface
                    ) {
                        Text(data.visuals.message)
                    }
                }
            },
        ) { paddingValues ->
            val focusManager = LocalFocusManager.current
            Surface(
                modifier = Modifier
                    .padding(
                        if (windowInfo.isBottomAppBarLayout())
                            paddingValues
                        else
                            PaddingValues(
                                0.dp,
                                paddingValues.calculateTopPadding(),
                                paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                                paddingValues.calculateBottomPadding()
                            )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { focusManager.clearFocus() }
                        )
                    }
            ) {
                Row {
                    if (!windowInfo.isBottomAppBarLayout())
                        MainNavigationRail(navController)
                    DestinationsNavHost(NavGraphs.root, navController = navController)
                }
            }
        }
    }
}