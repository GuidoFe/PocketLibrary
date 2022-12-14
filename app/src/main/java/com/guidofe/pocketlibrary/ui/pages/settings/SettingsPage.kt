package com.guidofe.pocketlibrary.ui.pages.settings

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.color.DynamicColors
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.dialogs.ThemeSelector
import com.guidofe.pocketlibrary.ui.dialogs.ThemeTile
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialog
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.DropdownBox
import com.guidofe.pocketlibrary.ui.pages.settings.SettingsState.WifiRequester
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.TranslationService
import com.guidofe.pocketlibrary.viewmodels.SettingsVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import com.guidofe.pocketlibrary.viewmodels.previews.SettingsVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsPage(
    vm: ISettingsVM = hiltViewModel<SettingsVM>(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val settings: AppSettings? by vm.settingsLiveData.observeAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val innerRowPadding = 10.dp
    val cm = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    }
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.settings)) },
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
    LaunchedEffect(settings) {
        settings?.let { vm.state.currentSettings = it }
    }
    Surface {
        vm.state.currentSettings?.let { s ->
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.language),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(innerRowPadding))
                    ExposedDropdownMenuBox(
                        expanded = true,
                        onExpandedChange = {
                            vm.state.isLanguageDropdownOpen = !vm.state.isLanguageDropdownOpen
                        },
                    ) {
                        DropdownBox(
                            text = { Text(s.language.localizedName) },
                            isExpanded = vm.state.isLanguageDropdownOpen,
                            modifier = Modifier.menuAnchor()
                        )
                        // It's necessary to hide the menu without using the expanded property,
                        // because the language change trigger the recreation of the activity and
                        // it glitches the animation of the closing dropdown. An alternative is
                        // setting a delay before the language change in vm
                        if (vm.state.isLanguageDropdownOpen) {
                            LanguageSettingDropdown(
                                settings = s,
                                connManager = cm,
                                closeDropdown = { vm.state.isLanguageDropdownOpen = false },
                                onWifiRequested = {
                                    vm.state.wifiRequester =
                                        WifiRequester.LanguageDropdown(it)
                                    vm.state.showAskForWifi = true
                                },
                                setLanguage = {
                                    vm.setLanguage(it)
                                }
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.allow_genres_translation),
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(innerRowPadding))
                    Switch(
                        checked = s.allowGenreTranslation,
                        onCheckedChange = {
                            if (!s.allowGenreTranslation && cm.isActiveNetworkMetered &&
                                s.language.code != "en"
                            ) {
                                TranslationService.hasTranslator(s.language.code) {
                                    if (it == null) {
                                        Log.e("debug", "HasTranslator returned null")
                                    } else {
                                        if (it)
                                            vm.setGenreTranslation(true)
                                        else {
                                            vm.state.wifiRequester =
                                                WifiRequester.TranslationSwitch
                                            vm.state.showAskForWifi = true
                                        }
                                    }
                                }
                            } else {
                                vm.setGenreTranslation(!s.allowGenreTranslation)
                            }
                        }
                    )
                }
                if (DynamicColors.isDynamicColorAvailable()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.dynamic_colors),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(innerRowPadding))
                        Switch(
                            checked = s.dynamicColors,
                            onCheckedChange = {
                                // vm.lastSettings = s
                                vm.setDynamicColors(!s.dynamicColors)
                            }
                        )
                    }
                }
                // TODO: Why changing dark mode while DynamicColors is active cause recomposition???
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.dark_theme),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(innerRowPadding))
                    Switch(
                        checked = s.darkTheme,
                        onCheckedChange = {
                            Log.d("debug", "Check changed")
                            // vm.lastSettings = s
                            vm.setDarkTheme(!s.darkTheme)
                        }
                    )
                }
                if (!s.dynamicColors) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.theme),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(innerRowPadding))
                        ThemeTile(theme = s.theme) {
                            vm.state.showThemeSelector = true
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.save_data_external),
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(innerRowPadding))
                    Switch(
                        checked = s.saveInExternal && vm.hasExternalStorage,
                        onCheckedChange = {
                            // vm.lastSettings = s
                            vm.setMemoryAndTransferFiles(!s.saveInExternal) { success ->
                                vm.state.showWaitForFileTransfer = false
                                if (success) {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.files_moved_successfully)
                                            )
                                        )
                                    }
                                } else {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.error_transfering_files),
                                                isError = true
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        enabled = vm.hasExternalStorage
                    )
                }
            }

            if (vm.state.showThemeSelector) {
                ThemeSelector(
                    themes = Theme.values().asList(),
                    currentTheme = s.theme,
                    onDismiss = {
                        vm.state.showThemeSelector = false
                    },
                    onClick = {
                        vm.setTheme(it)
                    }
                ) {
                    vm.state.showThemeSelector = false
                }
            }
        }
        if (vm.state.showWaitForFileTransfer) {
            Dialog(onDismissRequest = {}) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                ) {
                    Text(
                        stringResource(R.string.data_transfer_in_progress),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(stringResource(R.string.data_transfer_text))
                    if (vm.state.totalFiles != 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(20.dp)
                        ) {
                            Text(
                                "${vm.state.movedFiles}/${vm.state.totalFiles}",
                                modifier = Modifier.padding(10.dp)
                            )
                            LinearProgressIndicator(
                                progress = vm.state.movedFiles.toFloat() / vm.state.totalFiles,
                            )
                        }
                    }
                }
            }
        }
    }

    if (vm.state.showAskForWifi) {
        AlertDialog(
            onDismissRequest = {
                vm.state.wifiRequester = null
                vm.state.showAskForWifi = false
            },
            text = { Text(stringResource(R.string.ask_wifi_for_translation_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        when (vm.state.wifiRequester) {
                            is WifiRequester.LanguageDropdown -> vm.setLanguage(
                                (vm.state.wifiRequester as WifiRequester.LanguageDropdown).language
                            )
                            is WifiRequester.TranslationSwitch -> vm.setGenreTranslation(true)
                            else -> {}
                        }
                        vm.state.showAskForWifi = false
                    }
                ) {
                    Text(stringResource(R.string.download_anyway))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    if (vm.state.wifiRequester is WifiRequester.LanguageDropdown) {
                        val lang = (vm.state.wifiRequester as WifiRequester.LanguageDropdown)
                            .language
                        vm.setGenreTranslation(false)
                        vm.setLanguage(lang)
                    }
                    vm.state.showAskForWifi = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    TranslationDialog(vm.translationState)
}

@Composable
private fun LanguageSettingDropdown(
    settings: AppSettings,
    connManager: ConnectivityManager,
    closeDropdown: () -> Unit,
    onWifiRequested: (Language) -> Unit,
    setLanguage: (Language) -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = { closeDropdown() }
    ) {
        for (language in Language.values()) {
            DropdownMenuItem(text = {
                Text(language.localizedName)
            }, onClick = {
                closeDropdown()
                if (settings.allowGenreTranslation && connManager.isActiveNetworkMetered &&
                    language.code != "en"
                ) {
                    TranslationService.hasTranslator(language.code) {
                        if (it == null) {
                            Log.e("debug", "HasTranslator null")
                        } else {
                            if (it)
                                setLanguage(language)
                            else {
                                onWifiRequested(language)
                            }
                        }
                    }
                } else {
                    setLanguage(language)
                }
            })
        }
    }
}

@Composable
@Preview(showSystemUi = false, device = Devices.PIXEL_4)
private fun SettingsPagePreview() {
    PreviewUtils.ThemeColumn() {
        SettingsPage(SettingsVMPreview(), EmptyDestinationsNavigator)
    }
}
