package com.bedelln.iodine.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.Tray as ComposeTray
import androidx.compose.ui.window.MenuItem as ComposeMenuItem
import com.bedelln.iodine.*
import com.bedelln.iodine.desktop.ctx.SystemCtx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.awt.image.BufferedImage

class Tray<C: SystemCtx, E>(val icon: BufferedImage, val menuItems: List<MenuItem<C,E>>): ComponentDescription<C,E,Unit,Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<E, Unit, Unit> {
        return object: Component<E,Unit,Unit> {
            @Composable
            override fun contents() {
                DisposableEffect(Unit) {
                    val tray = ComposeTray().apply {
                        icon(icon)
                        menu(
                            *menuItems.map { mi ->
                                ComposeMenuItem(
                                    name = mi.name,
                                    onClick = {
                                        mi.action
                                    }
                                )
                            }.toTypedArray()
                        )
                    }
                    onDispose {
                        tray.remove()
                    }
                }
            }

            override val events: Flow<E>
                get() = TODO("Not yet implemented")
            override val result: StateFlow<Unit>
                get() = TODO()
        }
    }
}

data class MenuItem<C: SystemCtx, E>(val name: String, val action: ToolDescription<C,Unit,E>)