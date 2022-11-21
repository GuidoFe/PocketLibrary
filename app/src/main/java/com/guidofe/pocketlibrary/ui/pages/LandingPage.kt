package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.SettingsPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.LandingPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM
import com.guidofe.pocketlibrary.viewmodels.previews.LandingPageVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    vm: ILandingPageVM = hiltViewModel<LandingPageVM>(),
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            context.getString(R.string.home),
            actions = {
                IconButton(
                    onClick = {
                        navigator.navigate(SettingsPageDestination)
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.settings_24px),
                        stringResource(R.string.settings)
                    )
                }
            }
        )
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(
            EmptyDestinationsNavigator,
            LandingPageVMPreview()
        )
    }
}