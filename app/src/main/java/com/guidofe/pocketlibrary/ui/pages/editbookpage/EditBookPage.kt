package com.guidofe.pocketlibrary.ui.pages.editbookpage

import android.content.ActivityNotFoundException
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.TakeCoverPhotoPageDestination
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.EditBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import com.guidofe.pocketlibrary.viewmodels.previews.EditBookVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

val verticalSpace = 5.dp
val horizontalSpace = 5.dp
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun EditBookPage(
    bookId: Long? = null,
    isbn: String? = null,
    newBookDestination: BookDestination? = null,
    navigator: DestinationsNavigator,
    viewModel: IEditBookVM = hiltViewModel<EditBookVM>(),
    coverPhotoRecipient: ResultRecipient<TakeCoverPhotoPageDestination, Uri>
) {
    coverPhotoRecipient.onNavResult { result ->
        Log.d("debug", "EditPage received result")
        if (result is NavResult.Value) {
            Log.d("debug", "EditPage result is valid, == ${result.value}")
            viewModel.editBookState.coverUri = result.value
            Log.d("debug", "Reread, == ${result.value}")
        }
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageRequest: ImageRequest? by remember { mutableStateOf(null) }
    LaunchedEffect(viewModel.editBookState.coverUri) {
        imageRequest = viewModel.editBookState.coverUri?.let { uri ->
            ImageRequest.Builder(context)
                .data(uri)
                .build()
        }
    }
    BackHandler() {
        File(viewModel.getTempCoverUri().path!!).delete()
        navigator.navigateUp()
    }
    LaunchedEffect(key1 = true) {
        viewModel.scaffoldState.refreshBar(
            title = context.getString(R.string.edit_book),
            actions = {
                IconButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val id = viewModel.submitBook(newBookDestination)
                            if (id <= 0L) {
                                viewModel.snackbarHostState.showSnackbar(
                                    CustomSnackbarVisuals(
                                        context.getString(R.string.error_cant_save_book),
                                        true
                                    )
                                )
                            } else {
                                withContext(Dispatchers.Main) {
                                    navigator.navigateUp()
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        painterResource(id = R.drawable.check_24px),
                        stringResource(R.string.save)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    File(viewModel.getTempCoverUri().path!!).delete()
                    navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
        if (!viewModel.isInitialized) {
            bookId?.let { viewModel.initialiseFromDatabase(it) }
            isbn?.let {
                Log.d("debug", "Setting isbn $it")
                viewModel.editBookState.identifier = it
            }
            viewModel.isInitialized = true
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpace),
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        BoxWithConstraints {
            Box(
                modifier = Modifier
                    .clickable { viewModel.editBookState.showCoverMenu = true }
            ) {
                if (imageRequest != null) {
                    // TODO: placeholder for book cover
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = stringResource(id = R.string.cover),
                        Modifier.size(200.dp, 200.dp)
                    )
                } else
                    Image(
                        painterResource(id = R.drawable.sample_cover),
                        stringResource(R.string.cover)
                    )
            }
        }
        OutlinedTextField(
            value = viewModel.editBookState.title,
            label = { Text(stringResource(id = R.string.title) + "*") },
            onValueChange = { viewModel.editBookState.title = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.editBookState.subtitle,
            label = { Text(stringResource(id = R.string.subtitle)) },
            onValueChange = { viewModel.editBookState.subtitle = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.editBookState.authors,
            onValueChange = { viewModel.editBookState.authors = it },
            label = { Text(stringResource(R.string.authors)) },
            modifier = Modifier.fillMaxWidth()
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = viewModel.editBookState.genres) { genre ->
                InputChip(
                    selected = true,
                    onClick = {},
                    label = { Text(genre) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.editBookState.genres -= genre
                            },
                            modifier = Modifier.size(InputChipDefaults.IconSize)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24px),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedAutocomplete(
                text = viewModel.editBookState.genreInput,
                onTextChange = {
                    viewModel.editBookState.genreInput = it
                    if (it.length == 3)
                        viewModel.updateExistingGenres(it)
                },
                options = viewModel.editBookState.existingGenres,
                label = { Text(stringResource(R.string.new_genre)) },
                onOptionSelected = { viewModel.editBookState.genreInput = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (viewModel.editBookState.genreInput.isBlank())
                        return@IconButton
                    viewModel.editBookState.genres += viewModel.editBookState.genreInput
                    viewModel.editBookState.genreInput = ""
                }
            ) {
                Icon(painterResource(R.drawable.add_24px), stringResource(R.string.add))
            }
        }
        OutlinedTextField(
            value = viewModel.editBookState.description,
            onValueChange = { viewModel.editBookState.description = it },
            label = { Text(stringResource(id = R.string.summary)) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageAutocomplete(
                text = viewModel.editBookState.language,
                onTextChange = { viewModel.editBookState.language = it },
                onOptionSelected = { viewModel.editBookState.language = it },
                label = { Text(stringResource(R.string.language)) },
                isError = viewModel.editBookState.isLanguageError,
                modifier = Modifier
                    .weight(1f)
            )
            Column(
                // verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.is_ebook))
                Switch(
                    checked = viewModel.editBookState.isEbook,
                    onCheckedChange = {
                        viewModel.editBookState.isEbook = !viewModel.editBookState.isEbook
                    },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
        ) {
            OutlinedTextField(
                value = viewModel.editBookState.publisher,
                onValueChange = { viewModel.editBookState.publisher = it },
                label = { Text(stringResource(R.string.publisher)) },
                singleLine = true,
                modifier = Modifier
                    .weight(2f)
            )
            OutlinedTextField(
                value = viewModel.editBookState.published,
                onValueChange = {
                    viewModel.editBookState.published = it
                },
                label = { Text(stringResource(R.string.year)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
            )
        }
        OutlinedTextField(
            value = viewModel.editBookState.identifier,
            onValueChange = { viewModel.editBookState.identifier = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.isbn)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    ModalBottomSheet(
        visible = viewModel.editBookState.showCoverMenu,
        onDismiss = { viewModel.editBookState.showCoverMenu = false }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(R.drawable.photo_camera_24px),
                        stringResource(R.string.camera)
                    )
                },
                onClick = {
                    try {
                        val uri = viewModel.getTempCoverUri()
                        navigator.navigate(TakeCoverPhotoPageDestination(uri))
                    } catch (e: ActivityNotFoundException) {
                        coroutineScope.launch {
                            viewModel.snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    message = context.getString(R.string.error_no_camera),
                                    isError = true
                                )
                            )
                        }
                    }
                    viewModel.editBookState.showCoverMenu = false
                }
            ) {
                Text(stringResource(R.string.take_photo))
            }
            RowWithIcon(
                icon = {
                    Icon(painterResource(R.drawable.upload_24px), stringResource(R.string.upload))
                },
                onClick = { viewModel.editBookState.showCoverMenu = false }
            ) {
                Text(stringResource(R.string.choose_from_gallery))
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun ImportedBookFormPagePreview() {
    EditBookPage(
        bookId = 0,
        navigator = EmptyDestinationsNavigator,
        viewModel = EditBookVMPreview(),
        coverPhotoRecipient = EmptyResultRecipient()
    )
}