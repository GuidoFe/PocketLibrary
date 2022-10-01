package com.guidofe.pocketlibrary.ui.pages.librarypage

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.fab.FabMenuEntry
import com.guidofe.pocketlibrary.ui.modules.fab.MultiActionFab

@Composable
fun AddBookFab(onInsertIsbnManuallyClick: () -> Unit = {}) {
    MultiActionFab(
        fabIcon = painterResource(R.drawable.ic_baseline_add_24),
        fabIconDescription = stringResource(R.string.add_book),
        menuEntries = listOf(
            FabMenuEntry(
                stringResource(R.string.scan_isbn),
                painterResource(R.drawable.ic_scanner_24)
            ){

            },
            FabMenuEntry(
                stringResource(R.string.type_the_isbn),
                painterResource(R.drawable.ic_baseline_123_24),
                onClick = onInsertIsbnManuallyClick
            ),
            FabMenuEntry(
                stringResource(R.string.insert_manually),
                painterResource(R.drawable.ic_baseline_edit_note_24),
            ),
            FabMenuEntry(
                stringResource(R.string.import_ebook),
                painterResource(R.drawable.ic_baseline_borrowed_24)
            )
        )
    )
}