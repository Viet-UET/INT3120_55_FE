package com.example.quicknews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknews.data.model.UserAccount
import com.example.quicknews.data.remote.IdTokenRequest
import com.example.quicknews.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _userAccount = MutableStateFlow<UserAccount?>(null)
    val userAccount: StateFlow<UserAccount?> = _userAccount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.authApiService.googleLogin(IdTokenRequest(idToken))
                val userAccount = UserAccount(
                    displayName = response.user.name,
                    email = response.user.email,
                    photoUrl = response.user.picture,
                    jwtToken = response.token
                )
                _userAccount.value = userAccount
            } catch (e: Exception) {
                _error.value = "Failed to login with Google: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        _userAccount.value = null
    }
}
