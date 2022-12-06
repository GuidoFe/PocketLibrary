package com.guidofe.pocketlibrary.ui.pages.editbook

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val verticalSpace = 5.dp
val horizontalSpace = 5.dp
@OptIn(
    ExperimentalMaterial3Api::class
)
@Destination
@Composable
fun EditBookPage(
    bookId: Long? = null,
    isbn: String? = null,
    newBookDestination: BookDestination? = null,
    navigator: DestinationsNavigator,
    vm: IEditBookVM = hiltViewModel<EditBookVM>(),
    coverPhotoRecipient: ResultRecipient<TakeCoverPhotoPageDestination, Uri>
) {
    coverPhotoRecipient.onNavResult { result ->
        Log.d("debug", "EditPage received result")
        if (result is NavResult.Value) {
            Log.d("debug", "EditPage result is valid, == ${result.value}")
            vm.state.coverUri = result.value
            Log.d("debug", "Reread, == ${result.value}")
        }
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageRequest: ImageRequest? by remember { mutableStateOf(null) }
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val uploadFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        onResult = { result ->
            val tempUri = vm.getTempCoverUri()
            result?.let { uri ->
                val resolver = context.contentResolver
                resolver.openInputStream(uri).use { iStr ->
                    File(tempUri.path!!).outputStream().use { oStr ->
                        iStr?.copyTo(oStr)
                    }
                }
                vm.state.coverUri = tempUri
            }
        }
    )
    val readMediaPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            uploadFileLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }
    LaunchedEffect(vm.state.coverUri) {
        imageRequest = vm.state.coverUri?.let { uri ->
            ImageRequest.Builder(context)
                .data(uri)
                .build()
        }
    }
    BackHandler() {
        File(vm.getTempCoverUri().path!!).delete()
        navigator.navigateUp()
    }
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(
            title = context.getString(R.string.edit_book),
            actions = {
                IconButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val id = vm.submitBook(newBookDestination)
                            if (id <= 0L) {
                                vm.snackbarHostState.showSnackbar(
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
                    File(vm.getTempCoverUri().path!!).delete()
                    navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
        if (!vm.isInitialized) {
            bookId?.let { vm.initialiseFromDatabase(it) }
            isbn?.let {
                Log.d("debug", "Setting isbn $it")
                vm.state.identifier = it
            }
            vm.isInitialized = true
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpace),
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .nestedScroll(vm.scaffoldState.scrollBehavior!!.nestedScrollConnection)
            .padding(5.dp)
    ) {
        BoxWithConstraints {
            Box(
                modifier = Modifier
                    .clickable { vm.state.showCoverMenu = true }
            ) {
                if (imageRequest != null) {
                    // TODO: placeholder for book cover
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = stringResource(id = R.string.cover),
                        Modifier.height(200.dp)
                    )
                } else
                    EmptyBookCover(Modifier.width(90.dp))
            }
        }
        OutlinedTextField(
            value = vm.state.title,
            label = { Text(stringResource(id = R.string.title) + "*") },
            onValueChange = { vm.state.title = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vm.state.subtitle,
            label = { Text(stringResource(id = R.string.subtitle)) },
            onValueChange = { vm.state.subtitle = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vm.state.authors,
            onValueChange = { vm.state.authors = it },
            label = { Text(stringResource(R.string.authors)) },
            modifier = Modifier.fillMaxWidth()
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = vm.state.genres) { genre ->
                InputChip(
                    selected = true,
                    onClick = {},
                    label = { Text(genre) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                vm.state.genres -= genre
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
                text = vm.state.genreInput,
                onTextChange = {
                    vm.state.genreInput = it
                    if (it.length == 3)
                        vm.updateExistingGenres(it)
                },
                options = vm.state.existingGenres,
                label = { Text(stringResource(R.string.new_genre)) },
                onOptionSelected = { vm.state.genreInput = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (vm.state.genreInput.isBlank())
                        return@IconButton
                    vm.state.genres += vm.state.genreInput
                    vm.state.genreInput = ""
                }
            ) {
                Icon(painterResource(R.drawable.add_24px), stringResource(R.string.add))
            }
        }
        OutlinedTextField(
            value = vm.state.description,
            onValueChange = { vm.state.description = it },
            label = { Text(stringResource(id = R.string.summary)) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageAutocomplete(
                text = vm.state.language,
                onTextChange = { vm.state.language = it },
                onOptionSelected = { vm.state.language = it },
                label = { Text(stringResource(R.string.language)) },
                isError = vm.state.isLanguageError,
                modifier = Modifier
                    .weight(1f)
            )
            Column(
                // verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.is_ebook))
                Switch(
                    checked = vm.state.isEbook,
                    onCheckedChange = {
                        vm.state.isEbook = !vm.state.isEbook
                    },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace)
        ) {
            OutlinedTextField(
                value = vm.state.publisher,
                onValueChange = { vm.state.publisher = it },
                label = { Text(stringResource(R.string.publisher)) },
                singleLine = true,
                modifier = Modifier
                    .weight(2f)
            )
            OutlinedTextField(
                value = vm.state.published,
                onValueChange = {
                    vm.state.published = it
                },
                label = { Text(stringResource(R.string.year)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
            )
        }
        OutlinedTextField(
            value = vm.state.identifier,
            onValueChange = { vm.state.identifier = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.isbn)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    ModalBottomSheet(
        visible = vm.state.showCoverMenu,
        onDismiss = { vm.state.showCoverMenu = false }
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
                    val uri = vm.getTempCoverUri()
                    navigator.navigate(TakeCoverPhotoPageDestination(uri))
                } catch (e: ActivityNotFoundException) {
                    coroutineScope.launch {
                        vm.snackbarHostState.showSnackbar(
                            CustomSnackbarVisuals(
                                message = context.getString(R.string.error_no_camera),
                                isError = true
                            )
                        )
                    }
                }
                vm.state.showCoverMenu = false
            }
        ) {
            Text(stringResource(R.string.take_photo))
        }
        RowWithIcon(
            icon = {
                Icon(painterResource(R.drawable.upload_24px), stringResource(R.string.upload))
            },
            onClick = {
                readMediaPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                vm.state.showCoverMenu = false
            }
        ) {
            Text(stringResource(R.string.choose_from_gallery))
        }
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(
                        R.drawable.delete_24px
                    ),
                    stringResource(R.string.clear_cover)
                )
            },
            onClick = {
                vm.state.coverUri = null
                vm.state.showCoverMenu = false
            }
        ) {
            Text(stringResource(R.string.clear_cover))
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun ImportedBookFormPagePreview() {
    EditBookPage(
        bookId = 0,
        navigator = EmptyDestinationsNavigator,
        vm = EditBookVMPreview(),
        coverPhotoRecipient = EmptyResultRecipient()
    )
}