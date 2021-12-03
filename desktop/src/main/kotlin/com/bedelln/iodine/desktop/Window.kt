package com.bedelln.iodine.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import com.bedelln.iodine.desktop.ctx.SystemCtx
import com.bedelln.iodine.interfaces.ContainerRef
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.desktop.ctx.WindowRef
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.getContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

/** Entrypoint for an Iodine desktop application. */
fun iodineDesktopApplication(
    exitProcessOnExit: Boolean = true,
    content: @Composable SystemCtx.() -> Unit
) {
    application(exitProcessOnExit) {
        val appScope = this
        val trayState = rememberTrayState()

        val ctx = object: SystemCtx, ApplicationScope by appScope {
            override val trayState: TrayState
                get() = trayState
            override val defaultScope: CoroutineScope
                get() = CoroutineScope(Dispatchers.Default)
        }

        ctx.content()
    }
}

/**
 * Opens a window with the given component.
 */
@Composable
fun ApplicationScope.IodineWindow(
    title: String,
    contents: ComponentDescription<WindowCtx, *, *, Unit>
) {
    var isVisible by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = { isVisible = false },
        visible = isVisible,
        title = title,
    ) {
        windowContents(contents)
    }
}

@Composable
private fun <I,E> ApplicationScope.windowContents(
    contents: ComponentDescription<WindowCtx, I, E, Unit>
) {
    val trayState = rememberTrayState()
    val appScope = this
    var additional by remember { mutableStateOf(listOf<@Composable() () -> Unit>()) }
    val windowCtx = object : WindowCtx, ApplicationScope by appScope {
        override val window = object : WindowRef {
            override fun addToContents(f: @Composable() () -> Unit) {
                additional = additional + listOf(f)
            }
        }
        override val windowScope = GlobalScope

        override val trayState: TrayState
            get() = trayState
        override val defaultScope: CoroutineScope
            get() = windowScope
        override val ref: ContainerRef
            get() = window
    }

    contents.getContents(windowCtx, Unit)
    additional.forEach {
        it()
    }
}