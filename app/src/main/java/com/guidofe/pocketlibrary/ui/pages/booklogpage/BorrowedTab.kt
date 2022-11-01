package com.guidofe.pocketlibrary.ui.pages.booklogpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM

@Composable
fun BorrowedTab(
    borrowedItems: List<SelectableListItem<BorrowedBundle>>,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        LazyColumn() {
            items(borrowedItems) {

            }
        }
    }
}