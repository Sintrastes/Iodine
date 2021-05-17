package com.bedelln.iodine.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.Tray as ComposeTray
import androidx.compose.ui.window.MenuItem as ComposeMenuItem
import com.bedelln.iodine.*
import com.bedelln.iodine.desktop.ctx.SystemCtx
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.ToolDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.awt.image.BufferedImage

class Tray<C: SystemCtx, Ei, Eo>(val icon: BufferedImage, val menuItems: List<MenuItem<C,Eo>>): HComponentDescription<C,Ei,Eo,Unit,Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: Unit): HComponent<Ei, Eo, Unit, Unit> {
        return object: HComponent<Ei, Eo, Unit, Unit> {
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

            override val events: Flow<Eo>
                get() = TODO("Not yet implemented")
            override val result: StateFlow<Unit>
                get() = TODO()

            override fun onEvent(event: Ei) {

            }
        }
    }
}

data class MenuItem<C: SystemCtx, E>(val name: String, val action: ToolDescription<C, Unit, E>)