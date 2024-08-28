package com.fitsuli.glyphextender.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fitsuli.glyphextender.NavigationItems
import kotlinx.collections.immutable.toPersistentList

@Composable
fun NavigationBarWithItems(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        NavigationItems.entries.toPersistentList()
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedNavItem by remember {
        derivedStateOf {
            val routeHierarchy = navBackStackEntry?.destination?.hierarchy?.map { it.route }
            items.firstOrNull { screen ->
                routeHierarchy?.contains(screen.route::class.qualifiedName) == true
            } ?: NavigationItems.Home
        }
    }

    NavigationBar(modifier = modifier) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.imageVector,
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(screen.resourceId)) },
                selected = screen == selectedNavItem,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
