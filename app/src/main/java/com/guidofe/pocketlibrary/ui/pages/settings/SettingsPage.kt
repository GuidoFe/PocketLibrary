package com.guidofe.pocketlibrary.ui.pages.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val settings = vm.settingsFlow.collectAsState(
        initial = AppSettings(
            dynamicColors = context.resources?.configuration?.uiMode?.and(
                Configuration.UI_MODE_NIGHT_MASK
            ) == Configuration.UI_MODE_NIGHT_YES
        )
    ).value
    val scrollState = rememberScrollState()
    var isLanguageDropdownOpen by rememberSaveable { mutableStateOf(false) }
    var selectedLanguage by rememberSaveable { mutableStateOf(vm.getCurrentLanguageName()) }
    var isDynamicColorsEnabled by rememberSaveable { mutableStateOf(settings.dynamicColors) }
    var isDarkThemeEnabled by rememberSaveable { mutableStateOf(settings.darkTheme) }

    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(title = context.getString(R.string.settings))
    }
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
                    text = { Text(selectedLanguage) },
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
                                selectedLanguage = language.localizedName
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
                    checked = isDynamicColorsEnabled,
                    onCheckedChange = {
                        isDynamicColorsEnabled = !isDynamicColorsEnabled
                        vm.setDynamicColors(!settings.dynamicColors)
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
                checked = isDarkThemeEnabled,
                onCheckedChange = {
                    isDarkThemeEnabled = !isDarkThemeEnabled
                    vm.setDarkTheme(!settings.darkTheme)
                }
            )
        }
        /*
        if (!vm.state.dynamicTheme) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.theme),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.save_data_external),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = vm.state.saveInExternal,
                onCheckedChange = { vm.state.saveInExternal = !vm.state.saveInExternal },
                enabled = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            )
        }*/
    }
}