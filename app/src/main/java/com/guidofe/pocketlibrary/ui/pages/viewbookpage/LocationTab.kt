package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.OutlinedAutocomplete
import com.guidofe.pocketlibrary.viewmodels.ILocationViewModel
import com.guidofe.pocketlibrary.viewmodels.LocationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTab (
    bookId: Long,
    vm: ILocationViewModel = viewModel<LocationViewModel>()
) {
    val places by vm.places.collectAsState()
    val rooms by vm.possibleRooms.collectAsState()
    val bookshelves by vm.possibleBookshelves.collectAsState()
    Column() {
        OutlinedAutocomplete(
            text = vm.placeText,
            onTextChange = {
                vm.placeText = it
                vm.hasLocationBeenModified = true
                vm.changedPlace()
            },
            options = places,
            label = { Text(stringResource(R.string.place)) },
            onOptionSelected = {
                vm.placeText = it
                vm.hasLocationBeenModified = true
                vm.changedPlace()
            }
        )
        OutlinedAutocomplete(
            text = vm.roomText,
            onTextChange = {
                vm.roomText = it
                vm.hasLocationBeenModified = true
                vm.changedRoom()
            },
            options = rooms,
            label = { Text(stringResource(R.string.room)) },
            enabled = vm.placeText.isNotBlank(),
            onOptionSelected = {
                vm.roomText = it
                vm.hasLocationBeenModified = true
                vm.changedRoom()
            }
        )
        OutlinedAutocomplete(
            text = vm.bookshelfText,
            onTextChange = {
                vm.bookshelfText = it
                vm.hasLocationBeenModified = true
            },
            options = bookshelves,
            label = { Text(stringResource(R.string.bookshelf)) },
            enabled = vm.roomText.isNotBlank(),
            onOptionSelected = {
                vm.bookshelfText = it
                vm.hasLocationBeenModified = true
            }
        )
    }
    
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun LocationTabPreview() {
    MaterialTheme {
        Surface {
            LocationTab(0L, object: ILocationViewModel {
                override var placeText: String = "Home"
                override var roomText: String = "Bedroom"
                override var bookshelfText: String = ""
                override val places = MutableStateFlow<List<String>>(listOf())
                override val possibleRooms = MutableStateFlow<List<String>>(listOf())
                override val possibleBookshelves = MutableStateFlow<List<String>>(listOf())
                override var hasLocationBeenModified = true
                override fun setValues(place: String, room: String, bookshelf: String) {}
                override fun changedPlace() {}
                override fun changedRoom() {}
                override fun saveLocation(bookId: Long) {}
            })
        }
    }
}