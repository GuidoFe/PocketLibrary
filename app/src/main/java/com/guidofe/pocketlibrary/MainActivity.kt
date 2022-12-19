package com.guidofe.pocketlibrary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.guidofe.pocketlibrary.data.local.library_db.converters.UriConverter
import com.guidofe.pocketlibrary.notification.AppNotificationManager
import com.guidofe.pocketlibrary.ui.AppScreen
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.viewmodels.MainActivityVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settings by viewModel.settingsLiveData.observeAsState()
            PocketLibraryTheme(
                darkTheme = settings?.darkTheme ?: false,
                dynamicColor = settings?.dynamicColors ?: false,
                theme = settings?.theme ?: Theme.DEFAULT
            ) {
                settings?.let { s ->
                    val systemUiController = rememberSystemUiController()
                    val surfaceColor = MaterialTheme.colorScheme.surface
                    LaunchedEffect(surfaceColor) {
                        systemUiController.setStatusBarColor(
                            color = Color.Transparent,
                            darkIcons = surfaceColor.luminance() > 0.5f
                        )
                    }
                    LaunchedEffect(true) {
                        UriConverter.baseUri = viewModel.getCoverDir() ?: Uri.parse("")
                    }
                    AppScreen()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            AppNotificationManager.DUE_BOOKS_CHANNEL_NAME,
            this.getString(R.string.dueBooksChannelName),
            importance
        ).apply {
            description = getString(R.string.dueBooksChannelDescription)
        }
        val notificationManager = this.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
