package com.example.foodhub_android.ui.features.auth.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.auth.GoogleAuthUiProvider
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.data.models.SignInRequest
import com.example.foodhub_android.data.models.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {
    val googleAuthProvider = GoogleAuthUiProvider()
    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            try {
                val response = foodApi.signIn(
                    SignInRequest(
                        email = email.value,
                        password = password.value
                    )
                )

                if (response.token.isNotEmpty()) {
                    _uiState.value = SignInEvent.Success
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignInEvent.Error
            }
        }
    }

    fun onGoogleSignInClick(context: Context) {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = googleAuthProvider.signIn(
                context,
                CredentialManager.create(context)
            )
            if (response.isSuccess) {  // Changed to check isSuccess since we're returning Result
                val googleAccount = response.getOrNull()
                if (googleAccount != null) {
                    val request = OAuthRequest(
                        token = googleAccount.token,
                        provider = "google"
                    )
                    val res = foodApi.oAuth(request)
                    if (res.token.isNotEmpty()) {
                        Log.d("SignInViewModel", "onGoogleSignInClick: ${res.token}")
                        _uiState.value = SignInEvent.Success
                        _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                    }
                } else {
                    _uiState.value = SignInEvent.Error
                }
            } else {
                _uiState.value = SignInEvent.Error
            }
        }
    }

    fun onSignUpClicked(context: Context) {
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigateToSignUp)
        }
    }

    sealed class SignInNavigationEvent {
        object NavigateToSignUp : SignInNavigationEvent()
        object NavigateToHome : SignInNavigationEvent()
    }

    sealed class SignInEvent {
        object Nothing : SignInEvent()
        object Success : SignInEvent()
        object Error : SignInEvent()
        object Loading : SignInEvent()
    }
}
