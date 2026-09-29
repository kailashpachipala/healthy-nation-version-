package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.HealthyNationTheme
import com.example.ui.viewmodel.HealthyNationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthyNationTheme {
                val viewModel: HealthyNationViewModel = viewModel()
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
