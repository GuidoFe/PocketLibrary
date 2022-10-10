package com.guidofe.pocketlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.guidofe.pocketlibrary.ui.pages.MainPage
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.EditBookVM
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityVM by viewModels()
    private val editBookViewModel: EditBookVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PocketLibraryTheme(darkTheme = true) {
                MainPage()
            }
        }
    }


}
