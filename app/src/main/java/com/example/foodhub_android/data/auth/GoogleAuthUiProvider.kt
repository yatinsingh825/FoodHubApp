package com.example.foodhub_android.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.foodhub_android.data.models.GoogleAccount
import com.example.foodhub_android.ui.GoogleServerClientId
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleAuthUiProvider {
    suspend fun signIn(
        activityContext: Context,
        credentialManager: CredentialManager
    ): Result<GoogleAccount> = withContext(Dispatchers.IO) {
        try {
            val response = credentialManager.getCredential(
                activityContext,
                getCredentialRequest()
            )
            val result = handleCredentials(response.credential)
            Result.success(result)
        } catch (e: Exception) {
            Log.e("GoogleAuthUiProvider", "Error during sign in", e)
            Result.failure(e)
        }
    }

    private fun handleCredentials(creds: Credential): GoogleAccount {
        if (creds is CustomCredential && creds.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(creds.data)
                Log.d("GoogleAuthUiProvider", "GoogleIDTokenCredential: $googleIdTokenCredential")

                return GoogleAccount(
                    token = googleIdTokenCredential.idToken,
                    displayName = googleIdTokenCredential.displayName ?: "",
                    profileImageUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                )
            } catch (e: Exception) {
                throw IllegalStateException("Failed to parse Google ID token credential", e)
            }
        } else {
            throw IllegalArgumentException("Unsupported credential type: ${creds::class.java.simpleName}")
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(GoogleServerClientId)
                    .build()
            )
            .build()
    }
}

// Data class for GoogleAccount if you don't have it already
data class GoogleAccount(
    val token: String,
    val displayName: String,
    val profileImageUrl: String
)