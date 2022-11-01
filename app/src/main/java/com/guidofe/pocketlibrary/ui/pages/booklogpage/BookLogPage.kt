package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.BookLogVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun BookLogPage(
    vm: IBookLogVM = hiltViewModel<BookLogVM>()
) {
    var tabIndex: Int by remember{mutableStateOf(0)}
    val borrowedList by vm.borrowedItems.collectAsState(initial = listOf())
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = { Text(stringResource(R.string.borrowed)) }
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = { Text(stringResource(R.string.lent)) }
            )
        }

    }
}