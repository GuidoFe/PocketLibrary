package com.guidofe.pocketlibrary.ui.utils

import android.graphics.Bitmap
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import java.io.File
import java.sql.Date

class PreviewUtils {
    companion object {
        val exampleBookBundle = BookBundle(
            Book(
                bookId = 1,
                title = "The Lord of The Rings",
                subtitle = "The best story ever written",
                description = "A great story about hobbits",
                publisher = "Penguin",
                published = 1934,
                coverURI = null,
                identifier = "3245235423"
            ),
            listOf(Author(1, "J.R.R Tolkien"), Author(2, "Lewis")),
            genres = listOf(
                Genre(1, "Fantasy", "Fantasy", "en"),
                Genre(2, "Adventure", "Adventure", "en")
            ),
            note = Note(1, "It's a very good book")
        )

        val exampleLibraryBook = LibraryBook(
            1L,
            false,
        )

        val exampleLibraryBundle = LibraryBundle(
            exampleLibraryBook,
            exampleBookBundle,
            LentBook(1, "Mario", Date(System.currentTimeMillis()))
        )
        val exampleImportedBook = ImportedBookData(
            externalId = "id-234r2",
            title = "Very Interesting Book",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed " +
                "do eiusmod tempor incididunt ut labore et dolore magna aliqua. Suspendisse " +
                "sed nisi lacus sed. Risus in hendrerit gravida rutrum quisque. Vulputate " +
                "enim nulla aliquet porttitor lacus luctus accumsan tortor. Facilisis magna " +
                "etiam tempor orci eu lobortis elementum nibh tellus.",
            subtitle = "Short subtitle",
            publisher = "Adelphi",
            published = 1998,
            coverUrl = "https://m.media-amazon.com/images/I/51tAwFZt2XL.jpg",
            identifier = "987-9999999999",
            language = "en",
            authors = listOf("Pinco Pallino", "Giulio Cesare"),
            genres = listOf("Fantasy", "Humor")
        )

        @Composable
        fun ThemeRow(
            modifier: Modifier = Modifier,
            padding: Dp = 8.dp,
            content: @Composable () -> Unit
        ) {
            Row(modifier = modifier) {
                PocketLibraryTheme(darkTheme = false) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
                PocketLibraryTheme(darkTheme = true) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
            }
        }

        @Composable
        fun ThemeColumn(
            modifier: Modifier = Modifier,
            padding: Dp = 8.dp,
            content: @Composable () -> Unit
        ) {
            Column(modifier = modifier) {
                PocketLibraryTheme(darkTheme = false) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
                PocketLibraryTheme(darkTheme = true) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
            }
        }

        val emptySearchFieldManager = object : SearchFieldManager {
            override fun searchLogic() {
            }

            override var searchField: String = ""
            override var isSearching: Boolean = false
            override var shouldSearchBarRequestFocus: Boolean = true
        }

        @OptIn(ExperimentalMaterial3Api::class)
        val emptyScaffoldState = ScaffoldState().also {
            it.scrollBehavior = object : TopAppBarScrollBehavior {
                override val flingAnimationSpec: DecayAnimationSpec<Float>?
                    get() = null
                override val isPinned: Boolean
                    get() = true
                override val nestedScrollConnection: NestedScrollConnection
                    get() = object : NestedScrollConnection {}
                override val snapAnimationSpec: AnimationSpec<Float>?
                    get() = null
                override val state: TopAppBarState
                    get() = TopAppBarState(-Float.MAX_VALUE, 0f, 0f)
            }
        }
    }

    val fakeDataStoreRepository = object : DataStoreRepository {
        override suspend fun setLanguage(language: Language) {}

        override val settingsLiveData: LiveData<AppSettings>
            get() = liveData { AppSettings() }

        override suspend fun setDarkTheme(enabled: Boolean) {}

        override suspend fun setDynamicColors(enabled: Boolean) {}

        override suspend fun setTheme(theme: Theme) {}

        override suspend fun setMemory(isExternal: Boolean) {}

        override fun isExternalStorageWritable(): Boolean = true

        override val COVER_DIR: String
            get() = ""

        override fun getCoverDir(): File? = null

        override fun getCoverDir(isExternal: Boolean): File? = null

        override fun getCover(fileName: String): File? = null

        override fun getCoverPath(fileName: String): String? = null

        override suspend fun saveCover(bitmap: Bitmap, fullPath: String, callback: () -> Unit) {}

        override fun getExternalCoverFile(fileName: String): File? = null

        override fun getInternalCoverFile(fileName: String): File? = null

        override fun getCoverFile(fileName: String, external: Boolean): File? = null

        override suspend fun setGenreTranslation(translate: Boolean) {}

        override fun getDir(dir: String, isExternal: Boolean): File? = null

        override suspend fun saveMediaBackupLocally(external: Boolean): File? = null

        override fun getFileInRootDir(name: String, isExternal: Boolean): File? = null

        override val BACKUP_FILE_ROOT: String
            get() = ""

        override fun unzip(zipFilePath: File, destDirectory: String) {}

        override suspend fun zipFolder(folder: File, outputZipFile: File) {}
        override suspend fun setDefaultDaysBeforeDue(n: Int) {
        }

        override suspend fun setDefaultNotificationEnabled(enabled: Boolean) {
        }

        override suspend fun setDefaultNotificationTime(hours: Int, minutes: Int) {
        }
    }
}