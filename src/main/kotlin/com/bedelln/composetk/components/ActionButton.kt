package com.bedelln.composetk.components

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import com.bedelln.composetk.Component
import com.bedelln.composetk.ComponentDescription
import com.bedelln.composetk.Tool
import com.bedelln.composetk.desktop.WindowCtx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import com.bedelln.composetk.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ActionButton(val text: String, val action: ToolDescription<WindowCtx, Unit, Unit>): ComponentDescription<WindowCtx, Void, Unit, Unit> {
    override fun initialize(ctx: WindowCtx, initialValue: Unit): Component<Void, Unit, Unit> = run {
        val action = action.initialize(ctx, Unit)
        object : Component<Void, Unit, Unit> {
            @Composable
            override fun contents(input: Unit) {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        ctx.coroutineScope.launch {
                            action.runTool(ctx)
                        }
                    }
                )
            }

            override val events = emptyFlow<Void>()
            override val result = MutableStateFlow(Unit)
        }
    }
}