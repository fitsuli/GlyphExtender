package com.fitsuli.glyphextender.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleScaffold(
    headerText: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    // can be null if you don't want to use scrollable content
    scrollState: ScrollState? = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
    ),
    onBackPressed: (() -> Unit)? = null,
    contentWindowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DefLargeTopAppBar(
                title = headerText,
                onBackClicked = onBackPressed,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }, bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentWindowInsets = contentWindowInsets,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .then(
                        if (scrollState != null) {
                            Modifier
                                .verticalScroll(scrollState)
                                .padding(bottom = 72.dp)
                        } else {
                            Modifier
                        }
                    )
                    .padding(contentPadding),
//                    .navigationBarsPadding(),
                content = content
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefLargeTopAppBar(
    title: String,
    onBackClicked: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) = LargeTopAppBar(
    title = { Text(text = title) },
    navigationIcon = {
        if (onBackClicked != null) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior
)
