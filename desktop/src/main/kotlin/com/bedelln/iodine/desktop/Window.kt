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
fun <I,E,A> iodineApplication(
    initialValue: A,
    exitProcessOnExit: Boolean = true,
    content: ComponentDescription<SystemCtx, I, E, A>
) {
    application(exitProcessOnExit) {
        val appScope = this
        val trayState = rememberTrayState()

        val ctx = object: SystemCtx {
            override val trayState: TrayState
                get() = trayState
            override val appScope: ApplicationScope
                get() = appScope
            override val defaultScope: CoroutineScope
                get() = CoroutineScope(Dispatchers.Default)
        }

        content.initialize(ctx, initialValue)
            .getContents()
    }
}

/**
 * Entrypoint for an Iodine for Desktop application.
 *
 * Opens a window with the given component.
 *
 */
@Composable
fun <I,E> ApplicationScope.IodineWindow(
    title: String,
    contents: ComponentDescription<WindowCtx, I, E, Any>
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
    val windowCtx = object : WindowCtx {
        override val window = object : WindowRef {
            override fun addToContents(f: @Composable() () -> Unit) {
                additional = additional + listOf(f)
            }
        }
        override val windowScope = GlobalScope
        override val trayState: TrayState
            get() = trayState
        override val appScope: ApplicationScope
            get() = appScope
        override val defaultScope: CoroutineScope
            get() = windowScope
        override val ref: ContainerRef
            get() = window
    }
    contents.initCompose(windowCtx)
    contents.initialize(windowCtx, Unit)
        .getContents()
    additional.forEach {
        it()
    }
}