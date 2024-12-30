package com.example.foodhub_android

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.features.auth.signup.SignUpScreen
import com.example.foodhub_android.ui.theme.FoodHubAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var showSplashScreen by mutableStateOf(true)

    @Inject
    lateinit var foodApi: FoodApi

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize splash screen behavior
        splashScreen.setKeepOnScreenCondition {
            showSplashScreen
        }

        // Start countdown to hide splash screen
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // Show splash screen for 2 seconds
            showSplashScreen = false
        }

        splashScreen.setOnExitAnimationListener { screen ->
            // Animate icon
            val zoomX = ObjectAnimator.ofFloat(
                screen.iconView,
                View.SCALE_X,
                0.5f,
                0f
            )
            val zoomY = ObjectAnimator.ofFloat(
                screen.iconView,
                View.SCALE_Y,
                0.5f,
                0f
            )

            zoomX.apply {
                duration = 500
                interpolator = OvershootInterpolator()
                doOnEnd { screen.remove() }
            }

            zoomY.apply {
                duration = 500
                interpolator = OvershootInterpolator()
            }

            // Start animations
            zoomX.start()
            zoomY.start()
        }

        enableEdgeToEdge()
        setContent {
            FoodHubAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        SignUpScreen()
                    }
                }
            }
        }

        if (::foodApi.isInitialized) {
            Log.d("MainActivity", "FoodApi initialized")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodHubAndroidTheme {
        Greeting("Android")
    }
}