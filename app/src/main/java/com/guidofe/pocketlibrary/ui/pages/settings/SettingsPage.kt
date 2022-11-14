package com.guidofe.pocketlibrary.ui.pages.settings

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.dataStore
import com.guidofe.pocketlibrary.viewmodels.SettingsVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsPage(
    vm: ISettingsVM = hiltViewModel<SettingsVM>()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val appSettings = context.dataStore.data.collectAsState(initial = AppSettings()).value
    var isLanguageDropdownOpen by remember{mutableStateOf(false)}
    var languageSelected by remember{mutableStateOf("")}
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(title = context.getString(R.string.settings))
    }
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.language), modifier = Modifier.weight(1f))
            ExposedDropdownMenuBox(
                expanded = isLanguageDropdownOpen,
                onExpandedChange = {
                    isLanguageDropdownOpen = !isLanguageDropdownOpen
                }
            ) {
                OutlinedTextField(
                    value = languageSelected,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLanguageDropdownOpen)
                )
                DropdownMenu(
                    expanded = vm.state.isLanguageDropdownOpen,
                    onDismissRequest = { vm.state.isLanguageDropdownOpen = false }
                ) {
                    DropdownMenuItem(text = {
                        Text("English")
                    }, onClick = { vm.state.selectedLanguage = "English" })
                    DropdownMenuItem(text = {
                        Text("Italiano")
                    }, onCli qck = { vm.state.selectedLanguage = "Italiano" })
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.dynamic_theme),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = vm.state.dynamicTheme,
                    onCheckedChange = { vm.state.dynamicTheme = !vm.state.dynamicTheme }
                )
            }
        }
        if (!vm.state.dynamicTheme) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.follow_system_theme),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = vm.state.isFollowSystemSelected,
                    onCheckedChange = {
                        vm.state.isFollowSystemSelected = !vm.state.isFollowSystemSelected
                    }
                )
            }
            if (!vm.state.isFollowSystemSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.dark_theme),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = vm.state.darkTheme,
                        onCheckedChange = {
                            vm.state.darkTheme = !vm.state.darkTheme
                        }
                    )
                }
            }
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
        }
    }
}