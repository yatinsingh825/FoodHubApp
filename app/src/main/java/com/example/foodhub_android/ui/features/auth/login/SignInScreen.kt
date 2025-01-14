package com.example.foodhub_android.ui.features.auth.login

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_android.R
import com.example.foodhub_android.ui.FoodHubTextField
import com.example.foodhub_android.ui.GroupSocialButtons
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.navigation.AuthScreen
import com.example.foodhub_android.ui.navigation.Home
import com.example.foodhub_android.ui.navigation.SignUp
import com.example.foodhub_android.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreenRoute(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    SignInScreenContent(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun SignInScreenContent(
    navController: NavController,
    viewModel: SignInViewModel
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        val email = viewModel.email.collectAsStateWithLifecycle()
        val password = viewModel.password.collectAsStateWithLifecycle()
        val errorMessage = remember { mutableStateOf<String?>(null) }
        val loading = remember { mutableStateOf(false) }

        val uiState = viewModel.uiState.collectAsState()
        when (uiState.value) {
            is SignInViewModel.SignInEvent.Error -> {
                loading.value = false
                errorMessage.value = "Failed "
            }
            is SignInViewModel.SignInEvent.Loading -> {
                loading.value = true
                errorMessage.value = null
            }
            else -> {
                loading.value = false
                errorMessage.value = null
            }
        }

        LaunchedEffect(true) {
            viewModel.navigationEvent.collectLatest { event ->
                when (event) {
                    is SignInViewModel.SignInNavigationEvent.NavigateToHome -> {
                        navController.navigate(Home) {
                            popUpTo(AuthScreen) {
                                inclusive = true
                            }
                        }
                    }
                    is SignInViewModel.SignInNavigationEvent.NavigateToSignUp -> {
                        navController.navigate(SignUp)
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.sign_up),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.sign_in),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            FoodHubTextField(
                value = email.value,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text(text = stringResource(id = R.string.email), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )

            FoodHubTextField(
                value = password.value,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text(text = stringResource(id = R.string.password), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.size(16.dp))
            Text(text = errorMessage.value ?: "", color = Color.Red)

            Button(
                onClick = { viewModel.onSignInClick() },
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                    Crossfade(
                        targetState = loading.value,
                        label = "Loading Animation"
                    ) { isLoading ->
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.sign_in),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(id = R.string.donthave_an_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onSignUpClicked(context)
                    }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            GroupSocialButtons(
                color = Color.Black,
                onFacebookClick = {},
                onGoogleClick = {
                    viewModel.onGoogleSignInClick(context)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSignInScreen() {
    SignInScreenContent(
        navController = rememberNavController(),
        viewModel = hiltViewModel()
    )
}