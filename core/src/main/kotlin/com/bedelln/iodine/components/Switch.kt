package com.bedelln.iodine.components

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class Switch<C: IodineContext>(): HComponentDescription<C, Void, Void, Boolean, Boolean> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Boolean): HComponent<Void, Void, Boolean, Boolean> {
        return object: HComponent<Void, Void, Boolean, Boolean> {
            private val resultFlow = MutableStateFlow(initialValue)

            @Composable
            override fun contents() {
                val flowState = resultFlow.collectAsState()
                val state by remember { flowState }
                androidx.compose.material.Switch(
                    checked = state,
                    onCheckedChange = {
                        ctx.defaultScope.launch {
                            resultFlow.emit(it)
                        }
                    }
                )
            }

            override fun onEvent(event: Void) { }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Boolean>
                get() = resultFlow
        }
    }
}