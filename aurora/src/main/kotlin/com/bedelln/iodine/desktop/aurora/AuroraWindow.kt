package com.bedelln.iodine.desktop.aurora

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.rememberTrayState
import com.bedelln.iodine.desktop.aurora.ctx.AuroraCtx
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.getContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.pushingpixels.aurora.theming.AuroraSkinDefinition
import org.pushingpixels.aurora.window.AuroraApplicationScope
import org.pushingpixels.aurora.window.AuroraWindow
import org.pushingpixels.aurora.window.auroraApplication

fun auroraIodineApplication(
    content: @Composable AuroraApplicationScope.() -> Unit
) = auroraApplication {
    content()
}

/**
 * Opens a window with the given component.
 */
@Composable
fun AuroraApplicationScope.AuroraIodineWindow(
    skin: AuroraSkinDefinition,
    title: String,
    contents: ComponentDescription<AuroraCtx, *, *, Unit>
) {
    var isVisible by remember { mutableStateOf(true) }

    AuroraWindow(
        title = title,
        skin = skin,
        onCloseRequest = { isVisible = false },
        visible = isVisible
    ) {
        windowContents(contents)
    }
}

@Composable
private fun <I,E> AuroraApplicationScope.windowContents(
    contents: ComponentDescription<AuroraCtx, I, E, Unit>
) {
    val trayState = rememberTrayState()
    val appScope = this
    var additional by remember { mutableStateOf(listOf<@Composable() () -> Unit>()) }
    val windowCtx = object : AuroraCtx, ApplicationScope by appScope {
        override val scope: AuroraApplicationScope
            get() = appScope
        override val defaultScope: CoroutineScope
            get() = GlobalScope
    }

    contents.getContents(windowCtx, Unit)
    additional.forEach {
        it()
    }
}

