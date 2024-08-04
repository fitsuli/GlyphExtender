package com.fitsuli.glyphextender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fitsuli.glyphextender.ui.components.NavigationBarWithItems
import com.fitsuli.glyphextender.ui.navigation.Navigation
import com.fitsuli.glyphextender.ui.theme.GlyphExtenderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlyphExtenderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            NavigationBarWithItems(navController = navController)
                        }
                    ) { contentPadding ->
                        Navigation(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                        )
                    }
                }
            }
        }
    }
}

