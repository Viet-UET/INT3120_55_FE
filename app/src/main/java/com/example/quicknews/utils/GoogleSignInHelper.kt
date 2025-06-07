package com.example.quicknews.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.quicknews.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleSignInHelper(
    private val context: Context,
    private val authViewModel: AuthViewModel,
    private val launcher: ActivityResultLauncher<Intent>
) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("290069319038-bk1eenbrtfnbg09nojjc26k7td6r1rj7.apps.googleusercontent.com") // Thay CLIENT_ID Web
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                authViewModel.loginWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }
}
