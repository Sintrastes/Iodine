package com.bedelln.iodine.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.ToolDescription
import kotlinx.coroutines.launch

object Click

class ActionButton<C: IodineContext>(
    val text: String,
    val action: ToolDescription<C, Unit, Unit>
): HComponentDescription<C, Click, Void, Unit, Unit> {

    lateinit var setShow: () -> Unit

    @Composable
    override fun initCompose(ctx: C) {
        var initialized by remember { mutableStateOf(false) }
        setShow = { initialized = true }
        if (initialized) {
            action.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): HComponent<Click, Void, Unit, Unit> = run {
        object : HComponent<Click, Void, Unit, Unit> {
            @Composable
            override fun contents() {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        onEvent(Click)
                    }
                )
            }

            override fun onEvent(event: Click) {
                ctx.defaultScope.launch {
                    action.initialize(ctx, Unit).also {
                        setShow()
                    }
                        .runTool(ctx)
                }
            }

            override val events get()
                = emptyFlow<Void>()
            override val result get()
                = MutableStateFlow(Unit)
        }
    }
}