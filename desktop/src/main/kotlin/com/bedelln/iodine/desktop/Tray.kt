package com.bedelln.iodine.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.window.Tray as ComposeTray
import com.bedelln.iodine.desktop.ctx.SystemCtx
import com.bedelln.iodine.interfaces.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage

/**
 * Iodine component for displaying a system tray entry.
 */
/*
class Tray<C: SystemCtx, E>(
    val icon: BufferedImage,
    val menuItems: List<MenuItem<C,E>>
): ComponentDescription<C,Unit,E,Unit> {
    override fun initialize(ctx: C, initialValue: Unit): Component<Unit, E, Unit> {
        return object: ComponentImpl<Unit, E, Unit, Unit> {
            @Composable
            override fun contents(state: Unit) {
                ctx.ComposeTray(
                    icon = icon.toPainter(),
                    menu = {
                        menuItems.forEach { mi ->
                            Item(
                                text = mi.name,
                                onClick = {
                                    ctx.defaultScope.launch {
                                        mi.action.initialize(ctx, Unit)
                                            .runTool(ctx)
                                    }
                                }
                            )
                        }
                    }
                )
            }

            // TODO: Add events from tools.
            override val events: Flow<E>
                get() = emptyFlow()
            override val impl = Unit
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

/**
 * A menu item for a system tray component built with [Tray].
 */
data class MenuItem<C: SystemCtx, E>(
    /** The name displayed for the menu item. */
    val name: String,
    /** An action preformed when this menu item is clicked. */
    val action: ToolDescription<C, E, Unit, Unit>
)
 */
