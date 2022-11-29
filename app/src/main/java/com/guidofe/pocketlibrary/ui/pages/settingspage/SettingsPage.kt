package com.guidofe.pocketlibrary.ui.pages.settingspage

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
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.TranslationPhase
import com.guidofe.pocketlibrary.viewmodels.SettingsVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import com.guidofe.pocketlibrary.viewmodels.previews.SettingsVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsPage(
    vm: ISettingsVM = hiltViewModel<SettingsVM>()
) {
    val context = LocalContext.current
    val settings: AppSettings? by vm.settingsLiveData.observeAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val innerRowPadding = 10.dp
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(title = context.getString(R.string.settings))
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
                            DropdownMenu(
                                expanded = true,
                                onDismissRequest = { vm.state.isLanguageDropdownOpen = false }
                            ) {
                                for (language in Language.values()) {
                                    DropdownMenuItem(text = {
                                        Text(language.localizedName)
                                    }, onClick = {
                                        vm.state.isLanguageDropdownOpen = false
                                        // vm.lastSettings = s
                                        vm.setLanguage(language)
                                    })
                                }
                            }
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
                        onCheckedChange = { vm.setGenreTranslation(!s.allowGenreTranslation) }
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
    if (vm.translationState.translationPhase != TranslationPhase.NO_TRANSLATING) {
        TranslationDialog(vm.translationState)
    }
}

@Composable
@Preview(showSystemUi = false, device = Devices.PIXEL_4)
private fun SettingsPagePreview() {
    PreviewUtils.ThemeColumn() {
        SettingsPage(SettingsVMPreview())
    }
}
