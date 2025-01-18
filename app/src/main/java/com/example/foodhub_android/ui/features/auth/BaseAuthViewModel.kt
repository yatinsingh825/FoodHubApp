package com.example.foodhub_android.ui.features.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.auth.GoogleAuthUiProvider
import com.example.foodhub_android.data.models.OAuthRequest
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(private val foodApi: FoodApi) : ViewModel() {
    private val googleAuthProvider = GoogleAuthUiProvider()
    private lateinit var callbackManager: CallbackManager

    abstract fun loading()
    abstract fun onGoogleError(msg: String)
    abstract fun onFacebookError(msg: String)
    abstract fun onSocialLoginSuccess(token: String)

    fun onGoogleClicked(context: ComponentActivity) {
        initiateGoogleLogin(context)
    }

    fun onFacebookClicked(context: ComponentActivity) {
        initiateFacebookLogin(context)
    }

    private fun initiateGoogleLogin(context: ComponentActivity) {
        viewModelScope.launch {
            loading()
            try {
                val response = googleAuthProvider.signIn(
                    context,
                    CredentialManager.create(context)
                )
                if (response.isSuccess) {
                    val googleAccount = response.getOrNull()
                    if (googleAccount != null) {
                        val request = OAuthRequest(
                            token = googleAccount.token,
                            provider = "google"
                        )
                        try {
                            val res = foodApi.oAuth(request)
                            if (res.token.isNotEmpty()) {
                                onSocialLoginSuccess(res.token)
                            } else {
                                onGoogleError("Invalid token received")
                            }
                        } catch (e: Exception) {
                            onGoogleError("Failed to authenticate: ${e.message}")
                        }
                    } else {
                        onGoogleError("Google account information not available")
                    }
                } else {
                    onGoogleError("Google sign-in failed")
                }
            } catch (e: Exception) {
                onGoogleError("Google sign-in error: ${e.message}")
            }
        }
    }

    private fun initiateFacebookLogin(context: ComponentActivity) {
        loading()

        try {
            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        viewModelScope.launch {
                            try {
                                val request = OAuthRequest(
                                    token = loginResult.accessToken.token,
                                    provider = "facebook"
                                )
                                val res = foodApi.oAuth(request)
                                if (res.token.isNotEmpty()) {
                                    onSocialLoginSuccess(res.token)
                                } else {
                                    onFacebookError("Invalid token received")
                                }
                            } catch (e: Exception) {
                                onFacebookError("Failed to authenticate: ${e.message}")
                            }
                        }
                    }

                    override fun onCancel() {
                        onFacebookError("Login cancelled by user")
                    }

                    override fun onError(exception: FacebookException) {
                        onFacebookError("Login failed: ${exception.message}")
                    }
                })

            LoginManager.getInstance().logInWithReadPermissions(
                context,
                callbackManager,
                listOf("public_profile", "email")
            )
        } catch (e: Exception) {
            onFacebookError("Facebook login initialization failed: ${e.message}")
        }
    }
}