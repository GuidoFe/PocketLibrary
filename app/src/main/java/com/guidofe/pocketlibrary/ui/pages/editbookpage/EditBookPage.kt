package com.guidofe.pocketlibrary.ui.pages

import android.net.Uri
import android.util.Log
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
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.destinations.LandingPageDestination
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.viewmodels.EditBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val verticalSpace = 5.dp
val horizontalSpace = 5.dp
@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun EditBookPage(
   bookId: Long? = null,
   isbn: String? = null,
   navigator: DestinationsNavigator,
   viewModel: IEditBookVM = hiltViewModel<EditBookVM>()
) {
   val scrollState = rememberScrollState()
   val coroutineScope = rememberCoroutineScope()
   val context = LocalContext.current
   LaunchedEffect(key1 = true) {
      viewModel.appBarDelegate.setAppBarContent(
         AppBarState(
            title=context.getString(R.string.edit_book),
            actions = {
               IconButton(
                  onClick = {
                     coroutineScope.launch(Dispatchers.IO) {
                        val id = viewModel.submitBook()
                        //TODO: check if id >= 0
                        withContext(Dispatchers.Main) {
                           navigator.navigate(LandingPageDestination)
                        }
                     }
                  }
               ) {
                  Icon(
                     painterResource(id = R.drawable.check_24px),
                     stringResource(R.string.save)
                  )
               }
            }
         )
      )
      bookId?.let {viewModel.initialiseFromDatabase(it)}
      isbn?.let { Log.d("debug", "Setting isbn $it"); viewModel.formData.identifier = it}
   }
   Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(verticalSpace),
      modifier = Modifier
         .verticalScroll(scrollState)
         .fillMaxWidth()
         .padding(5.dp)
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
      OutlinedTextField(
         value = viewModel.formData.title,
         label = { Text(stringResource(id = R.string.title) + "*") },
         onValueChange = { viewModel.formData.title = it },
         modifier = Modifier.fillMaxWidth()
      )
      OutlinedTextField(
         value = viewModel.formData.subtitle,
         label = { Text(stringResource(id = R.string.subtitle)) },
         onValueChange = { viewModel.formData.subtitle = it },
         modifier = Modifier.fillMaxWidth()
      )
      OutlinedTextField(
         value = viewModel.formData.authors,
         onValueChange = { viewModel.formData.authors = it },
         label = { Text(stringResource(R.string.authors)) },
         modifier = Modifier.fillMaxWidth()
      )
      OutlinedTextField(
         value = viewModel.formData.description,
         onValueChange = { viewModel.formData.description = it },
         label = { Text(stringResource(id = R.string.summary)) },
         modifier = Modifier.fillMaxWidth()
      )
      Row(
         horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
      ) {
         OutlinedTextField(
            value = viewModel.formData.language,
            onValueChange = { viewModel.formData.language = it },
            label = { Text(stringResource(R.string.language)) },
            singleLine = true,
            modifier = Modifier
               .weight(1f)
         )
         OutlinedTextField(
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
         OutlinedTextField(
            value = viewModel.formData.publisher,
            onValueChange = { viewModel.formData.publisher = it },
            label = { Text(stringResource(R.string.publisher)) },
            singleLine = true,
            modifier = Modifier
               .weight(2f)
         )
         OutlinedTextField(
            value = viewModel.formData.published.toString(),
            onValueChange = {
               try {
                  viewModel.formData.published = it
               } catch (e: NumberFormatException) {
                  //TODO: Manage exception
               }
            },
            label = { Text(stringResource(R.string.year)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
               .weight(1f)
         )
      }
      OutlinedTextField(
         value = viewModel.formData.identifier,
         onValueChange = { viewModel.formData.identifier = it },
         singleLine = true,
         label = { Text(stringResource(R.string.isbn)) },
      )
   }
}

private object VMPreview: IEditBookVM {
    override var formData: FormData =
        FormData(
           title = "Dune",
           subtitle = "The Greatest Sci-Fi Story",
           description = "Opposing forces struggle for control of the universe when the archenemy of the cosmic emperor is banished to a barren world where savages fight for water",
           publisher = "Penguin",
           published = 1990,
           coverUri = Uri.parse("http://books.google.com/books/content?id=nrRKDwAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"),
           identifier = "9780441172719",
           language = "en",
           authors   = "Frank Herbert, Princess Irulan",
           genres    = listOf("Fantasy", "Sci-fi"),
        )


   override suspend fun initialiseFromDatabase(id: Long) {
   }

   override suspend fun submitBook() {}
   override val appBarDelegate = PreviewUtils.fakeAppBarStateDelegate
}

@Composable
@Preview(showSystemUi = true)
fun ImportedBookFormPagePreview() {
   EditBookPage(
      bookId = 0,
      navigator = EmptyDestinationsNavigator,
      viewModel = VMPreview
      )
}