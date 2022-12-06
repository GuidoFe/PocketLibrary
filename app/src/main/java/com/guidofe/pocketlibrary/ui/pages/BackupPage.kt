package com.guidofe.pocketlibrary.ui.pages
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.viewmodels.BackupPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBackupPageVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun BackupPage(
    vm: IBackupPageVM = hiltViewModel<BackupPageVM>(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val loginLauncher = rememberLauncherForActivityResult(
        object : ActivityResultContract<Int, Intent?>() {
            override fun createIntent(context: Context, input: Int): Intent {
                vm.initRepo(context)
                return vm.getIntent()!!
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
                return intent
            }
        },
        onResult = {
            if (it == null) {
                Log.e("debug", "Intent is null")
            } else {
                vm.handleSignInData(
                    it,
                    onError = { e ->
                        e.printStackTrace()
                        coroutineScope.launch {
                            vm.snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    context.getString(R.string.error_login_gdrive),
                                    true
                                )
                            )
                        }
                    },
                    onPermissionsNotGranted = {
                        coroutineScope.launch {
                            vm.snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    context.getString(R.string.error_perm_not_granted_gdrive),
                                    true
                                )
                            )
                        }
                    },
                    onSuccess = {
                        coroutineScope.launch {
                            vm.snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    "Successfully logged in",
                                )
                            )
                        }
                    }
                )
            }
        }
    )
    LaunchedEffect(true) {
        vm.initRepo(context)
        vm.scaffoldState.refreshBar(
            context.getString(R.string.backup_restore),
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
    }
    val scroll = rememberScrollState()
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scroll)
        ) {
            Text(
                stringResource(R.string.backup_intro),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(
                        if (vm.isLoggedInState)
                            R.string.sign_out
                        else
                            R.string.login_on_gdrive
                    ),
                    modifier = Modifier.weight(1f)
                )
                OutlinedIconButton(
                    onClick = {
                        if (vm.isLoggedIn()) {
                            vm.signOut(
                                onComplete = {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.sign_out_success)
                                            )
                                        )
                                    }
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.sign_out_failed),
                                                isError = true
                                            )
                                        )
                                    }
                                }
                            )
                        } else {
                            loginLauncher.launch(vm.CONST_SIGN_IN)
                        }
                    }
                ) {
                    if (vm.isLoggedInState) {
                        Icon(
                            painter = painterResource(R.drawable.logout_24px),
                            contentDescription = stringResource(id = R.string.sign_out),
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.gdrive_icon),
                            contentDescription = stringResource(id = R.string.login_on_gdrive),
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            if (!vm.isLoggedInState) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        stringResource(R.string.first_log_in_error),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(5.dp)
                    )
                    Divider(Modifier.weight(1f))
                }
            }
            CompositionLocalProvider(
                LocalContentColor provides if (vm.isLoggedInState)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.outline
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.backup_covers),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedIconButton(
                        onClick = {
                            vm.backupMedia(
                                onSuccess = {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.covers_upload_success),
                                            )
                                        )
                                    }
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                context.getString(R.string.covers_upload_failed),
                                                isError = true
                                            )
                                        )
                                    }
                                }
                            )
                        },
                        enabled = vm.isLoggedInState
                    ) {
                        Icon(
                            painterResource(R.drawable.upload_24px),
                            stringResource(R.string.backup_covers)
                        )
                    }
                }
            }
        }
    }
}