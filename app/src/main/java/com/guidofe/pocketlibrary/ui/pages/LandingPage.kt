package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.LandingPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination(start = true)
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    vm: ILandingPageVM = hiltViewModel<LandingPageVM>(),
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(context.getString(R.string.home))
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(
            EmptyDestinationsNavigator,
            object: ILandingPageVM {
                override val scaffoldState: ScaffoldState
                    = ScaffoldState()
            }
        )
    }
}