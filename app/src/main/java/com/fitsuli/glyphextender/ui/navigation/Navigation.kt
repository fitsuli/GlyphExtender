package com.fitsuli.glyphextender.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fitsuli.glyphextender.ui.screens.home.HomeScreen
import soup.compose.material.motion.animation.materialSharedAxisYIn
import soup.compose.material.motion.animation.materialSharedAxisYOut
import soup.compose.material.motion.animation.rememberSlideDistance

/**
 * Created by Dmitry Danilyuk at 04.08.2024
 */
@Composable
fun Navigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val slideDistance = rememberSlideDistance()

    NavHost(
        navController = navController,
        startDestination = Destinations.Home,
        modifier = modifier,
        enterTransition = {
            materialSharedAxisYIn(forward = true, slideDistance = slideDistance)
        },
        exitTransition = {
            materialSharedAxisYOut(forward = true, slideDistance = slideDistance)
        },
        popEnterTransition = {
            materialSharedAxisYIn(forward = false, slideDistance = slideDistance)
        },
        popExitTransition = {
            materialSharedAxisYOut(forward = false, slideDistance = slideDistance)
        }
    ) {
        composable<Destinations.Home> {
            HomeScreen()
        }
        composable<Destinations.Progress> {
//            ProgressScreen()
        }
        composable<Destinations.Notifications> {
//            NotificationsScreen()
        }
        composable<Destinations.Settings> {
//            SettingsScreen()
        }
        composable<Destinations.About> {
//            AboutScreen()
        }
    }
}
