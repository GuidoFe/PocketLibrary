package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.OutlinedAutocomplete
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILocationVM


@Composable
fun LocationTab (
    vm: ILocationVM
) {
    Column() {
        OutlinedAutocomplete(
            text = vm.placeText,
            onTextChange = {
                vm.placeText = it
                vm.hasLocationBeenModified = true
                vm.changedPlace()
            },
            options = vm.places,
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
            options = vm.possibleRooms,
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
            options = vm.possibleBookshelves,
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
private fun LocationTabPreview() {
    MaterialTheme {
        Surface {
            LocationTab(object: ILocationVM {
                override var placeText: String = ""
                override var roomText: String = ""
                override var bookshelfText: String = ""
                override val places: List<String> = listOf()
                override val possibleRooms: List<String> = listOf()
                override val possibleBookshelves: List<String> = listOf()
                override var hasLocationBeenModified: Boolean = false

                override fun setPlaceValues(place: String, room: String, bookshelf: String) {}

                override fun changedPlace() { }

                override fun changedRoom() {}

                override fun saveLocation(bookId: Long) {}

            })
        }
    }
}