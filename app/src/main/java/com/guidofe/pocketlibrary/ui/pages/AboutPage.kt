package com.guidofe.pocketlibrary.ui.pages
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.BuildConfig
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.BasicPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBasicPageVM
import com.guidofe.pocketlibrary.viewmodels.previews.BasicPageVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AboutPage(
    vm: IBasicPageVM = hiltViewModel<BasicPageVM>(),
    navigator: DestinationsNavigator
) {
    val uriHandler = LocalUriHandler.current
    val email = "guido.ferri@protonmail.com"
    var text by remember { mutableStateOf("Very long text") }
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.about)) },
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
    val scroll = rememberScrollState()
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            OutlinedTextField(value = text, onValueChange = { text = it })
            ResourcesCompat.getDrawable(
                LocalContext.current.resources,
                R.mipmap.ic_launcher_round, LocalContext.current.theme
            )?.let { drawable ->
                val bitmap = remember {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                Image(
                    bitmap.asImageBitmap(),
                    stringResource(R.string.logo),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.created_by_me))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedIconButton(
                    onClick = { uriHandler.openUri("mailto:$email") }
                ) {
                    Icon(
                        painterResource(R.drawable.mail_24px),
                        stringResource(R.string.mail)
                    )
                }
                OutlinedIconButton(
                    onClick = { uriHandler.openUri("https://github.com/GuidoFe") }
                ) {
                    Icon(
                        painterResource(R.drawable.gitub_logo),
                        stringResource(R.string.github)
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    stringResource(R.string.privacy_policy),
                    color = MaterialTheme.colorScheme.outline,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable {
                        uriHandler.openUri(
                            "https://guidofe.github.io/PocketLibraryApp/" +
                                "privacypolicy.html"
                        )
                    }
                )
                Text(
                    stringResource(R.string.terms_and_conditions),
                    color = MaterialTheme.colorScheme.outline,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable {
                        uriHandler.openUri(
                            "https://guidofe.github.io/PocketLibraryApp/" +
                                "terms.html"
                        )
                    }
                )
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun CreditsPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        AboutPage(
            navigator = EmptyDestinationsNavigator,
            vm = BasicPageVMPreview()
        )
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun CreditsPagePreviewDark() {
    PocketLibraryTheme(darkTheme = true) {
        AboutPage(
            navigator = EmptyDestinationsNavigator,
            vm = BasicPageVMPreview()
        )
    }
}