package com.bedelln.composetk.desktop

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key.Companion.Window
import androidx.compose.ui.window.Notifier
import com.bedelln.composetk.ComponentDescription
import com.bedelln.composetk.desktop.ctx.SystemCtx
import com.bedelln.composetk.desktop.ctx.WindowCtx
import com.bedelln.composetk.desktop.ctx.WindowRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

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
        override val notifier get() = Notifier()
        override val windowScope = GlobalScope
    }
    contents.initCompose(windowCtx)
    contents.initialize(windowCtx, Unit)
        .contents()
    additional.forEach {
        it()
    }
}