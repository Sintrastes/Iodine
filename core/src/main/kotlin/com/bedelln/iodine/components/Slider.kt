package com.bedelln.iodine.components

import androidx.compose.runtime.*
import androidx.compose.material.Slider
import androidx.compose.ui.Modifier
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Slider(
    val modifier: Modifier = Modifier
): SFormDescription<IodineContext, Slider.Action, Void, Float> {
    interface Action {
        fun setPosition(position: Float)
        fun currentPosition(): Float
    }

    override fun initialize(ctx: IodineContext, initialValue: Float) =
        object: FormImpl<Action, Void, Float, Float, Float> {

            private val contentsFlow = MutableStateFlow(initialValue)
            override val state get() = contentsFlow

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
            override val result: StateFlow<Float>
                get() = contentsFlow
        }
}