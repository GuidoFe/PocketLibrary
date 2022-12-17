package com.guidofe.pocketlibrary.ui.pages.editbook

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.TakeCoverPhotoPageDestination
import com.guidofe.pocketlibrary.ui.utils.Menu
import com.guidofe.pocketlibrary.ui.utils.MenuItem
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.utils.isPermanentlyDenied
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

val verticalSpace = 8.dp
val horizontalSpace = 8.dp
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class
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
        } else {
            Log.e("debug", "EditPage result is not valid")
        }
    }
    val scrollState = rememberScrollState()
    val windowInfo = rememberWindowInfo()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageRequest: ImageRequest? by remember { mutableStateOf(null) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
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
    val permissionState = rememberPermissionState(
        permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionResult = {
            if (it) {
                uploadFileLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    )
    val contextMenu = remember {
        Menu<Unit>(
            menuItems = arrayOf(
                MenuItem(
                    labelId = { R.string.camera },
                    iconId = { R.drawable.photo_camera_24px },
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
                    }
                ),
                MenuItem(
                    labelId = { R.string.choose_from_gallery },
                    iconId = { R.drawable.upload_24px },
                    onClick = {
                        when {
                            permissionState.status.isGranted -> {
                                Log.d("debug", "Permission granted")
                                permissionState.launchPermissionRequest()
                            }
                            permissionState.status.shouldShowRationale -> {
                                showRationaleDialog = true
                            }
                            permissionState.status.isPermanentlyDenied -> {
                                showPermissionDeniedDialog = true
                            }
                        }
                    }
                ),
                MenuItem(
                    labelId = { R.string.clear_cover },
                    iconId = { R.drawable.delete_24px },
                    onClick = { vm.state.coverUri = null }
                )
            )
        )
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    coroutineScope.launch(Dispatchers.IO) {
                        bookId?.let { id ->
                            val previousUri = vm.state.coverUri
                            vm.initialiseFromDatabase(id)
                            previousUri?.let { vm.state.coverUri = it }
                        }
                        isbn?.let {
                            Log.d("debug", "Setting isbn $it")
                            vm.state.identifier = it
                        }
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    File(vm.getTempCoverUri().path!!).delete()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(vm.state.coverUri) {
        imageRequest = vm.state.coverUri?.let { uri ->
            ImageRequest.Builder(context)
                .data(uri)
                .build()
        }
    }
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.edit_book)) },
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
        if (windowInfo.isBottomSheetLayout()) {
            vm.scaffoldState.bottomSheetContent = {
                for (line in contextMenu.menuItems) {
                    RowWithIcon(
                        icon = {
                            Icon(
                                painterResource(
                                    line.iconId(Unit)
                                ),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            vm.scaffoldState.setBottomSheetVisibility(false, coroutineScope)
                            line.onClick(Unit)
                        }
                    ) {
                        Text(stringResource(line.labelId(Unit)))
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .nestedScroll(vm.scaffoldState.scrollBehavior.nestedScrollConnection)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(verticalSpace),
            modifier = Modifier
                .verticalScroll(scrollState)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter)
        ) {
            BoxWithConstraints {
                Box(
                    modifier = Modifier
                        .clickable {
                            if (windowInfo.isBottomSheetLayout())
                                vm.scaffoldState.setBottomSheetVisibility(
                                    true, coroutineScope
                                )
                            else {
                                showDropdown = true
                            }
                        }
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
                if (!windowInfo.isBottomSheetLayout()) {
                    DropdownMenu(showDropdown, onDismissRequest = { showDropdown = false }) {
                        for (line in contextMenu.menuItems) {
                            DropdownMenuItem(
                                text = { Text(stringResource(line.labelId(Unit))) },
                                onClick = {
                                    showDropdown = false
                                    line.onClick(Unit)
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(line.iconId(Unit)),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = vm.state.title,
                label = { Text(stringResource(id = R.string.title) + "*") },
                onValueChange = { vm.state.title = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.state.subtitle,
                label = { Text(stringResource(id = R.string.subtitle)) },
                singleLine = true,
                onValueChange = { vm.state.subtitle = it },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.state.authors,
                onValueChange = { vm.state.authors = it },
                singleLine = true,
                label = { Text(stringResource(R.string.authors)) },
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
    }
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            confirmButton = {
                Button(onClick = { showRationaleDialog = false }) {
                    Text(stringResource(R.string.deny))
                }
            },
            title = { Text(stringResource(R.string.permission_required)) },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        permissionState.launchPermissionRequest()
                        showRationaleDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ask_again))
                }
            },
            text = { Text(stringResource(R.string.gallery_rationale)) }
        )
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            confirmButton = {
                Button(onClick = { showPermissionDeniedDialog = false }) {
                    Text(stringResource(R.string.ok_label))
                }
            },
            text = { Text(stringResource(R.string.gallery_denied)) }
        )
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