package com.example.paragi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.paragi.ui.theme.ParagiTheme
import com.example.paragi.view.AddDebtScreen
import com.example.paragi.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            ParagiTheme {

                NavHost(navController, startDestination = "main_screen"){
                    composable ("main_screen"){
                        MainScreen(navController)
                    }

                    composable("add_debt"){
                        AddDebtScreen(navController)
                    }
                }
            }
        }
    }
}

