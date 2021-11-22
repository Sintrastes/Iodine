package com.bedelln.iodine.components

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.material.Slider
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Slider(): SFormDescription<IodineContext, Slider.Action, Void, Float> {
    interface Action {
        fun setPosition(position: Float)
        fun currentPosition(): Float
    }

    override fun initialize(ctx: IodineContext, initialValue: Float): Form<Action, Void, Float, Float> {
        return object: Form<Action, Void, Float, Float> {

            private val contentsFlow = MutableStateFlow(initialValue)
            override val state = contentsFlow

            @Composable
            override fun contents(state: Float) {
                val st = result.collectAsState()
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
            override val result: StateFlow<Float>
                get() = contentsFlow
        }
    }
}