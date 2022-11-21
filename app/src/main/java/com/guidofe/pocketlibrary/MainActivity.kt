package com.guidofe.pocketlibrary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.guidofe.pocketlibrary.ui.pages.MainPage
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settingsLiveData.observeAsState()
            PocketLibraryTheme(
                darkTheme = settings?.darkTheme ?: false,
                dynamicColor = settings?.dynamicColors ?: false,
                theme = settings?.theme ?: Theme.DEFAULT
            ) {
                MainPage()
            }
        }
    }
}
