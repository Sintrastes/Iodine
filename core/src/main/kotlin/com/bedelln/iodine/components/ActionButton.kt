package com.bedelln.iodine.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class ActionButton<C: IodineContext>(
    val text: String,
    val action: ToolDescription<C, Unit, Unit>
): ComponentDescription<C, ActionButton.Action, Void, Unit> {

    interface Action {
        fun click()
    }

    lateinit var setShow: () -> Unit

    @Composable
    override fun initCompose(ctx: C) {
        var initialized by remember { mutableStateOf(false) }
        setShow = { initialized = true }
        if (initialized) {
            action.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Action, Void, Unit> = run {
        object : ComponentImpl<Action, Void, Unit, Unit> {
            @Composable
            override fun contents(state: Unit) {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        impl.click()
                    }
                )
            }

            override val impl = object: Action {
                override fun click() {
                    ctx.defaultScope.launch {
                        action.initialize(ctx, Unit).also {
                            setShow()
                        }
                            .runTool(ctx)
                    }
                }
            }

            override val events get()
                = emptyFlow<Void>()
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}