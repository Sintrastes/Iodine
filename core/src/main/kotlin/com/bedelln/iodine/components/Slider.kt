package com.bedelln.iodine.components

import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.material.Slider
import com.bedelln.iodine.interfaces.ComponentImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Slider(): ComponentDescription<IodineContext, Slider.Action, Void, Float> {
    interface Action {
        fun setPosition(position: Float)
        fun currentPosition(): Float
    }

    override fun initialize(ctx: IodineContext, initialValue: Float): Component<Action, Void, Float> {
        return object: ComponentImpl<Action, Void, Float, Float> {

            private val contentsFlow = MutableStateFlow(initialValue)
            override val state = contentsFlow

            @Composable
            override fun contents(state: Float) {
                Slider(
                    value = state,
                    onValueChange = {
                        ctx.defaultScope.launch {
                            contentsFlow.emit(it)
                        }
                    }
                )
            }

            override val impl = object: Action {
                override fun setPosition(position: Float) {
                    ctx.defaultScope.launch {
                        contentsFlow.emit(position)
                    }
                }

                override fun currentPosition(): Float {
                    return contentsFlow.value
                }
            }
            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}