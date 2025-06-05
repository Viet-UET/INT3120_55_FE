package com.example.quicknews.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknews.R // Quan trọng: import R để truy cập string resources
import com.example.quicknews.ui.viewmodel.AuthViewModel // Sẽ tạo ở bước sau
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val googleSignInClient = remember { Identity.getSignInClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = googleSignInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    Log.d("LoginScreen", "Got ID Token: $idToken")
                    authViewModel.signInWithGoogleToken(idToken, onLoginSuccess)
                } else {
                    Log.e("LoginScreen", "ID Token is null")
                    authViewModel.setError("Google ID Token was null.")
                }
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Google Sign-In failed: ${e.localizedMessage}", e)
                authViewModel.setError("Google Sign-In failed: ${e.localizedMessage}")
            }
        } else {
             Log.e("LoginScreen", "Google Sign-In launcher failed with resultCode: ${result.resultCode}")
             authViewModel.setError("Google Sign-In was cancelled or failed.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                authViewModel.setLoading(true)
                authViewModel.setError(null)

                val serverClientId = context.getString(R.string.default_web_client_id)
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(serverClientId) // Web Client ID
                            .setFilterByAuthorizedAccounts(false) // Show all Google accounts
                            .build()
                    )
                    .setAutoSelectEnabled(false) // Cho phép tự động chọn nếu chỉ có 1 tài khoản đã đăng nhập
                    .build()

                coroutineScope.launch {
                    try {
                        val result = googleSignInClient.beginSignIn(signInRequest).await()
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        launcher.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        // Xử lý lỗi khi beginSignIn, ví dụ không có Google Play Services
                        Log.e("LoginScreen", "Begin sign in failed: ${e.localizedMessage}", e)
                        authViewModel.setError("Could not initiate Google Sign-In: ${e.localizedMessage}")
                        authViewModel.setLoading(false)
                    }
                }
            }) {
                Text("Sign in with Google")
            }
        }

        error?.let {
            Text(
                text = "Error: $it",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Helper extension for Task
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result!!, null)
            } else {
                continuation.cancel(task.exception)
            }
        }
    }
}