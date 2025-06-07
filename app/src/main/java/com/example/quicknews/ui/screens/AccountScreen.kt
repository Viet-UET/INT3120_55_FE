package com.example.quicknews.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknews.ui.viewmodel.AuthViewModel
import com.example.quicknews.utils.GoogleSignInHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    authViewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val userAccount by authViewModel.userAccount.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity ?: return

    // Tạo biến googleSignInHelper mutableState
    var googleSignInHelper by remember {
        mutableStateOf<GoogleSignInHelper?>(null)
    }

    // Tạo launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        googleSignInHelper?.handleSignInResult(data)
    }

    // Khởi tạo googleSignInHelper
    LaunchedEffect(Unit) {
        googleSignInHelper = GoogleSignInHelper(activity, authViewModel, googleSignInLauncher)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                }
                userAccount == null -> {
                    Text(text = "Not signed in")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        googleSignInHelper?.startGoogleSignIn()
                    }) {
                        Text("Sign In with Google")
                    }
                }
                else -> {
                    userAccount?.let { user ->
                        Text(text = "Name: ${user.displayName}")
                        Text(text = "Email: ${user.email}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { authViewModel.signOut() }) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }
}
