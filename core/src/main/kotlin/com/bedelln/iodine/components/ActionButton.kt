package com.bedelln.iodine.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import com.bedelln.iodine.*
import kotlinx.coroutines.launch

class ActionButton<C: IodineContext>(val text: String, val action: ToolDescription<C, Unit, Unit>): ComponentDescription<C, Void, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        action.initCompose(ctx)
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Void, Unit, Unit> = run {
        object : Component<Void, Unit, Unit> {
            @Composable
            override fun contents() {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        ctx.defaultScope.launch {
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