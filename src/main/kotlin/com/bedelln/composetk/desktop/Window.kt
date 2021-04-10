package com.bedelln.composetk.desktop

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key.Companion.Window
import com.bedelln.composetk.ComponentDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

interface WindowRef {
    fun addToContents(f: @Composable() () -> Unit)
}

interface WindowCtx {
    val window: WindowRef
    val coroutineScope: CoroutineScope
}

fun ComposeTkWindow(
    title: String,
    contents: ComponentDescription<WindowCtx, Void, Unit, Unit>
) {
    Window(title) {
        windowContents(contents)
    }
}

@Composable
private fun windowContents(contents: ComponentDescription<WindowCtx, Void, Unit, Unit>) {
    var additional by remember { mutableStateOf(listOf<@Composable() () -> Unit>()) }
    val windowCtx = object : WindowCtx {
        override val window = object : WindowRef {
            override fun addToContents(f: @Composable() () -> Unit) {
                additional = additional + listOf(f)
            }
        }
        override val coroutineScope = GlobalScope
    }
    contents.initialize(windowCtx, Unit)
        .contents()
    additional.forEach {
        it()
    }
}