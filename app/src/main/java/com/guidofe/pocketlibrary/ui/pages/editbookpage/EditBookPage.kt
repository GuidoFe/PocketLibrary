package com.guidofe.pocketlibrary.ui.pages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.destinations.LandingPageDestination
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookPageNavArgs
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.EditBookViewModel
import com.guidofe.pocketlibrary.viewmodels.IEditBookViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val verticalSpace = 5.dp
val horizontalSpace = 5.dp
@OptIn(ExperimentalMaterial3Api::class)
@Destination(navArgsDelegate = EditBookPageNavArgs::class)
@Composable
fun EditBookPage(
   navigator: DestinationsNavigator,
   viewModel: IEditBookViewModel = hiltViewModel<EditBookViewModel>()
) {
   val scrollState = rememberScrollState()
   val coroutineScope = rememberCoroutineScope()
   val context = LocalContext.current
   LaunchedEffect(key1 = true) {
      viewModel.appBarDelegate.setAppBarContent(
         AppBarState(title=context.getString(R.string.edit_book)
         )
      )
   }
   Scaffold() { innerPadding ->
      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.spacedBy(verticalSpace),
         modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .padding(innerPadding)
      ) {
         if (viewModel.formData.coverUri != null) {
            //TODO: placeholder for book cover
            AsyncImage(
               model = ImageRequest.Builder(LocalContext.current)
                  .data(viewModel.formData.coverUri)
                  .build(),
               contentDescription = stringResource(id = R.string.cover),
               Modifier.size(200.dp, 200.dp)
            )
         } else
            Image(
               painterResource(id = R.drawable.sample_cover),
               stringResource(R.string.cover)
            )
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
            ) {
               Text(stringResource(R.string.owned))
               Checkbox(
                  checked = viewModel.formData.isOwned,
                  onCheckedChange = { viewModel.formData.isOwned = it }
               )
            }
            TextField(
               value = viewModel.formData.progress.toString(),
               onValueChange = {},
               readOnly = true,
               label = { Text(stringResource(R.string.progress)) }
            )
         }
         TextField(
            value = viewModel.formData.title,
            label = { Text(stringResource(id = R.string.title) + "*") },
            onValueChange = { viewModel.formData.title = it },
            modifier = Modifier.fillMaxWidth()
         )
         TextField(
            value = viewModel.formData.subtitle,
            label = { Text(stringResource(id = R.string.subtitle)) },
            onValueChange = { viewModel.formData.subtitle = it },
            modifier = Modifier.fillMaxWidth()
         )
         TextField(
            value = viewModel.formData.authors,
            onValueChange = { viewModel.formData.authors = it },
            label = { Text(stringResource(R.string.authors)) },
            modifier = Modifier.fillMaxWidth()
         )
         TextField(
            value = viewModel.formData.description,
            onValueChange = { viewModel.formData.description = it },
            label = { Text(stringResource(id = R.string.description)) },
            modifier = Modifier.fillMaxWidth()
         )
         Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
         ) {
            TextField(
               value = viewModel.formData.language,
               onValueChange = { viewModel.formData.language = it },
               label = { Text(stringResource(R.string.language)) },
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
            TextField(
               value = viewModel.formData.media.toString(),
               onValueChange = {},
               label = { Text(stringResource(R.string.type)) },
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
         }
         Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
         ) {
            TextField(
               value = viewModel.formData.publisher,
               onValueChange = { viewModel.formData.publisher = it },
               label = { Text(stringResource(R.string.publisher)) },
               singleLine = true,
               modifier = Modifier
                  .weight(2f)
            )
            TextField(
               value = viewModel.formData.published.toString(),
               onValueChange = {
                  try {
                     viewModel.formData.published = it
                  } catch (e: NumberFormatException) {
                     //TODO: Manage exception
                  }
               },
               label = { Text(stringResource(R.string.published_year)) },
               singleLine = true,
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
               modifier = Modifier
                  .weight(1f)
            )
         }
         Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
            modifier = Modifier.fillMaxWidth()
         ) {
            TextField(
               value = viewModel.formData.identifierType.toString(),
               onValueChange = {},
               readOnly = true,
               trailingIcon = {
                  ExposedDropdownMenuDefaults.TrailingIcon(
                     expanded = false
                  )
               },
               label = { Text(stringResource(R.string.identifier_type)) },
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
            TextField(
               value = viewModel.formData.identifier,
               onValueChange = { viewModel.formData.identifier = it },
               singleLine = true,
               label = { Text(stringResource(R.string.isbn)) },
               modifier = Modifier
                  .weight(1f)
            )
         }
         TextField(
            value = viewModel.formData.place,
            onValueChange = { viewModel.formData.place = it },
            label = { Text(stringResource(id = R.string.place)) },
            placeholder = { Text(stringResource(id = R.string.placeExample)) },
            modifier = Modifier.fillMaxWidth()
         )
         TextField(
            value = viewModel.formData.room,
            onValueChange = { viewModel.formData.room = it },
            label = { Text(stringResource(id = R.string.room)) },
            placeholder = { Text(stringResource(id = R.string.roomExample)) },
            enabled = viewModel.formData.place.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
         )
         TextField(
            value = viewModel.formData.bookshelf,
            onValueChange = { viewModel.formData.bookshelf = it },
            label = { Text(stringResource(id = R.string.bookshelf)) },
            placeholder = { Text(stringResource(id = R.string.bookshelfExample)) },
            enabled = viewModel.formData.place.isNotBlank() && viewModel.formData.room.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
         )
         TextField(
            value = viewModel.formData.note,
            onValueChange = { viewModel.formData.note = it },
            label = { Text(stringResource(id = R.string.note)) },
            modifier = Modifier.fillMaxWidth()
         )
         Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
               val id = viewModel.submitBook()
               //TODO: check if id >= 0
               withContext(Dispatchers.Main) {
                  navigator.navigate(LandingPageDestination)
               }
            }
         }) {
            Text(text = "Submit")
         }
      }
   }
}

private object ViewModelPreview: IEditBookViewModel {
    override var formData: FormData by mutableStateOf(
        FormData(
            title = "Dune",
            subtitle = "The Greatest Sci-Fi Story",
            description = "Opposing forces struggle for control of the universe when the archenemy of the cosmic emperor is banished to a barren world where savages fight for water",
            publisher = "Penguin",
            published = "1990",
            coverUri = Uri.parse("http://books.google.com/books/content?id=nrRKDwAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"),
            identifierType = IndustryIdentifierType.ISBN_13,
            identifier = "9780441172719",
            language = "en",
            authors   = "Frank Herbert, Princess Irulan",
            genres    = listOf("Fantasy", "Sci-fi"),
            place     = "Home",
            room      = "",
            bookshelf = "Big library",
            note      = "Very cool book"
        )
    )

   override fun initializeFromImportedBook(importedBook: ImportedBookData) {
   }

   override suspend fun initialiseFromDatabase(bookBundle: BookBundle) {
   }

   override suspend fun submitBook() {}
   override val appBarDelegate: AppBarStateDelegate =
      AppBarStateDelegate(MutableStateFlow(AppBarState()))
}

@Composable
@Preview(showSystemUi = true)
fun ImportedBookFormPagePreview() {
   EditBookPage(
      navigator = EmptyDestinationsNavigator,
      viewModel = ViewModelPreview
      )
}