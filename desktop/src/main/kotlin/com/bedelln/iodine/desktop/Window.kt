package com.bedelln.iodine.desktop

import androidx.compose.desktop.Window
import androidx.compose.runtime.*
import androidx.compose.ui.window.Notifier
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.interfaces.ContainerRef
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.desktop.ctx.WindowRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * Entrypoint for an Iodine for Desktop application.
 *
 * Opens a window with the given component.
 *
 */
fun IodineWindow(
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
        override val defaultScope: CoroutineScope
            get() = windowScope
        override val ref: ContainerRef
            get() = window
    }
    contents.initCompose(windowCtx)
    contents.initialize(windowCtx, Unit)
        .contents()
    additional.forEach {
        it()
    }
}