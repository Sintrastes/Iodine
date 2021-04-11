package com.bedelln.iodine.desktop

import androidx.compose.desktop.Window
import androidx.compose.runtime.*
import androidx.compose.ui.window.Notifier
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.desktop.ctx.WindowRef
import kotlinx.coroutines.GlobalScope

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