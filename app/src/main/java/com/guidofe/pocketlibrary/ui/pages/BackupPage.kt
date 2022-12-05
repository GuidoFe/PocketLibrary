package com.guidofe.pocketlibrary.ui.pages
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(stringResource(R.string.backup_on_gd))
                IconButton(
                    onClick = {
                        vm.initRepo(context)
                        if (!vm.isLoggedIn()) {
                            loginLauncher.launch(vm.CONST_SIGN_IN)
                        } else {
                            Log.w("debug", "Already logged in")
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.upload_24px),
                        contentDescription = stringResource(id = R.string.backup_on_gd)
                    )
                }
            }
        }
    }
}