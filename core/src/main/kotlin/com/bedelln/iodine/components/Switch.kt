package com.bedelln.iodine.components

import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.ComponentImpl
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * Iodine component for a single [androidx.compose.material.Switch].
 */
class Switch<C: IodineContext>(): ComponentDescription<C, Switch.Action, Void, Boolean> {
    interface Action {
        fun toggle()
        fun isOn(): Boolean
    }

    override fun initialize(ctx: C, initialValue: Boolean): Component<Action, Void, Boolean> {
        return object: ComponentImpl<Action, Void, Boolean, Boolean> {
            private val resultFlow = MutableStateFlow(initialValue)
            override val state get() = resultFlow

            @Composable
            override fun contents(state: Boolean) {
                androidx.compose.material.Switch(
                    checked = state,
                    onCheckedChange = {
                        ctx.defaultScope.launch {
                            resultFlow.emit(it)
                        }
                    }
                )
            }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val impl = object: Action {
                override fun toggle() {
                    ctx.defaultScope.launch {
                        resultFlow.emit(!resultFlow.value)
                    }
                }

                override fun isOn(): Boolean {
                    return state.value
                }
            }
        }
    }
}