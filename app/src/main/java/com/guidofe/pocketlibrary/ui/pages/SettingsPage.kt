package com.guidofe.pocketlibrary.ui.pages.settings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.color.DynamicColors
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.DropdownBox
import com.guidofe.pocketlibrary.ui.modules.ThemeSelector
import com.guidofe.pocketlibrary.ui.modules.ThemeTile
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.viewmodels.SettingsVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import com.ramcosta.composedestinations.annotation.Destination
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
    var isLanguageDropdownOpen by rememberSaveable { mutableStateOf(false) }
    var showThemeSelector by rememberSaveable { mutableStateOf(false) }
    var currentSettings: AppSettings? by remember { mutableStateOf(null) }
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(title = context.getString(R.string.settings))
    }
    LaunchedEffect(settings) {
        settings?.let { currentSettings = it }
    }
    currentSettings?.let { s ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
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
                ExposedDropdownMenuBox(
                    expanded = true,
                    onExpandedChange = {
                        isLanguageDropdownOpen = !isLanguageDropdownOpen
                    },
                ) {
                    DropdownBox(
                        text = { Text(s.language.localizedName) },
                        isExpanded = isLanguageDropdownOpen,
                        modifier = Modifier.menuAnchor()
                    )
                    // It's necessary to hide the menu without using the expanded property, because the
                    // language change trigger the recreation of the activity and it glitches the
                    // animation of the closing dropdown. An alternative is setting a delay before the
                    // language change in vm
                    if (isLanguageDropdownOpen) {
                        DropdownMenu(
                            expanded = true,
                            onDismissRequest = { isLanguageDropdownOpen = false }
                        ) {
                            for (language in Language.values()) {
                                DropdownMenuItem(text = {
                                    Text(language.localizedName)
                                }, onClick = {
                                    isLanguageDropdownOpen = false
                                    // vm.lastSettings = s
                                    vm.setLanguage(language)
                                })
                            }
                        }
                    }
                }
            }
            if (DynamicColors.isDynamicColorAvailable()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.dynamic_colors),
                        modifier = Modifier.weight(1f)
                    )
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
                    ThemeTile(theme = s.theme) {
                        showThemeSelector = true
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.save_data_external),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = s.saveInExternal && vm.hasExternalStorage,
                    onCheckedChange = {
                        // vm.lastSettings = s
                        vm.setMemory(!s.saveInExternal)
                    },
                    enabled = vm.hasExternalStorage
                )
            }
        }

        if (showThemeSelector) {
            ThemeSelector(
                themes = Theme.values().asList(),
                currentTheme = s.theme,
                onDismiss = {
                    showThemeSelector = false
                },
                onClick = {
                    vm.setTheme(it)
                }
            ) {
                showThemeSelector = false
            }
        }
    }
}