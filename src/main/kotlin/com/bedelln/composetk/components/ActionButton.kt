package com.bedelln.composetk.components

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import com.bedelln.composetk.Component
import com.bedelln.composetk.ComponentDescription
import com.bedelln.composetk.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import com.bedelln.composetk.*
import com.bedelln.composetk.desktop.ctx.WindowCtx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ActionButton(val text: String, val action: ToolDescription<WindowCtx, Unit, Unit>): ComponentDescription<WindowCtx, Void, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: WindowCtx) {
        action.initCompose(ctx)
    }

    override fun initialize(ctx: WindowCtx, initialValue: Unit): Component<Void, Unit, Unit> = run {
        object : Component<Void, Unit, Unit> {
            @Composable
            override fun contents() {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        ctx.windowScope.launch {
                            action.initialize(ctx, Unit)
                                .runTool(ctx)
                        }
                    }
                )
            }

            override val events get()
                = emptyFlow<Void>()
            override val result get()
                = MutableStateFlow(Unit)
        }
    }
}