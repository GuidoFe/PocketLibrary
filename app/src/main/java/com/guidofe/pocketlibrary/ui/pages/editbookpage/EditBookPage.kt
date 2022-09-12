package com.guidofe.pocketlibrary.ui.pages

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.viewmodels.EditBookViewModel
import com.guidofe.pocketlibrary.model.FormData
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookPageNavArgs
import com.ramcosta.composedestinations.annotation.Destination
import java.lang.NumberFormatException

val verticalSpace = 5.dp
val horizontalSpace = 5.dp
@Destination(navArgsDelegate = EditBookPageNavArgs::class)
@ExperimentalMaterialApi
@Composable
fun EditBookPage(
      viewModel: EditBookViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   BoxWithConstraints(
   ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.spacedBy(verticalSpace),
         modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .padding(20.dp)
      ) {
         if(viewModel.formData.coverUri.value != null) {
            //TODO: placeholder for book cover
            var url: String = viewModel.formData.coverUri.value.toString()
            url = url.replaceFirst("http", "https")
            Log.d("test", url)
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
               .data(url)
               .build(),
               contentDescription = viewModel.formData.title.value,
               Modifier.size(200.dp, 200.dp)
            )
         } else
            Image(painterResource(id = R.drawable.sample_cover),
               viewModel.formData.title.value)
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
                  checked = viewModel.formData.isOwned.value,
                  onCheckedChange = { viewModel.formData.isOwned.value = it }
               )
            }
            OutlinedTextField(
               value = viewModel.formData.progress.value.toString(),
               onValueChange = {},
               readOnly = true,
               label = {Text(stringResource(R.string.progress))}
            )
         }
         OutlinedTextField(
            value = viewModel.formData.title.value,
            label = { Text(stringResource(id = R.string.title)+"*") },
            onValueChange = { viewModel.formData.title.value = it },
            modifier = Modifier.fillMaxWidth()
         )
         OutlinedTextField(
            value = viewModel.formData.subtitle.value,
            label = { Text(stringResource(id = R.string.subtitle))},
            onValueChange = { viewModel.formData.subtitle.value = it },
            modifier = Modifier.fillMaxWidth()
         )
         OutlinedTextField(
            value = viewModel.formData.authors.joinToString(),
            onValueChange = {},
            label = {Text(stringResource(R.string.authors))},
            modifier = Modifier.fillMaxWidth()
         )
         OutlinedTextField(
            value = viewModel.formData.description.value,
            onValueChange = {viewModel.formData.description.value = it},
            label = {Text(stringResource(id = R.string.description))},
            modifier = Modifier.fillMaxWidth()
         )
         Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
         ) {
            OutlinedTextField(
               value = viewModel.formData.language.value,
               onValueChange = {viewModel.formData.language.value = it},
               label = { Text(stringResource(R.string.language)) },
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
            OutlinedTextField(
               value = viewModel.formData.media.value.toString(),
               onValueChange = {},
               label = {Text(stringResource(R.string.type))},
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
         }
         Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
         ) {
            OutlinedTextField(
               value = viewModel.formData.publisher.value,
               onValueChange = {viewModel.formData.publisher.value = it},
               label = {Text(stringResource(R.string.publisher))},
               singleLine = true,
               modifier = Modifier
                  .weight(2f)
            )
            OutlinedTextField(
               value = viewModel.formData.published.value.toString(),
               onValueChange = {
                  try {
                     viewModel.formData.published.value = it.toInt()
                  } catch(e:NumberFormatException){
                  }},
               label = {Text(stringResource(R.string.published_year))},
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
            OutlinedTextField(
               value = viewModel.formData.identifierType.value.toString(),
               onValueChange = {},
               readOnly = true,
               trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(
                  expanded = false
               )},
               label = {Text(stringResource(R.string.identifier_type))},
               singleLine = true,
               modifier = Modifier
                  .weight(1f)
            )
            OutlinedTextField(
               value = viewModel.formData.identifier.value,
               onValueChange = {viewModel.formData.identifier.value = it},
               singleLine = true,
               label = {Text(stringResource(R.string.isbn))},
               modifier = Modifier
                  .weight(1f)
            )
         }
         OutlinedTextField(
            value = viewModel.formData.note.value,
            onValueChange = {viewModel.formData.note.value = it},
            label = {Text(stringResource(id = R.string.note))},
            modifier = Modifier.fillMaxWidth()
         )
         Button(onClick = { viewModel.submitBook()}) {
            Text(text = "Submit")
         }
      }
   }
}

@ExperimentalMaterialApi
@Composable
fun EditBookPageContent() {

}

@ExperimentalMaterialApi
@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun ImportedBookFormPagePreview() {
   val formData = FormData()
   formData.title.value = "Dune"
   formData.subtitle.value = "The Greatest Sci-Fi Story"
   formData.description.value = "Opposing forces struggle for control of the universe when the archenemy of the cosmic emperor is banished to a barren world where savages fight for water"
   formData.publisher.value = "Penguin"
   formData.published.value = 1990
   formData.coverUri.value = Uri.parse("http://books.google.com/books/content?id=nrRKDwAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
   formData.identifierType.value = IndustryIdentifierType.ISBN_13
   formData.identifier.value = "9780441172719"
   formData.language.value = "en"
   formData.authors.addAll(
      listOf(
         Author(-1, "Frank Herbert"),
         Author(-1, "Frank Imagination"),
         Author(-1, "Paul Muad'Dib"),
         Author(-1, "Gurney Hallek"),
         Author(-1, "Princess Irulan")))
   //formData.genre = listOf("Fiction", "Sci-Fi", "Drama")
   EditBookPageContent()
}