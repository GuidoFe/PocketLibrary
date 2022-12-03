package com.guidofe.pocketlibrary.ui.pages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.BuildConfig
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.BasicPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBasicPageVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CreditsPage(
    vm: IBasicPageVM = hiltViewModel<BasicPageVM>(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            context.getString(R.string.about),
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
    }
    Surface() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(5.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_launcher_foreground),
                    stringResource(R.string.logo),
                    tint = Color.Unspecified
                )
            }
            Text(stringResource(R.string.app_name))
            Text("v${BuildConfig.VERSION_NAME}")
        }
    }
}