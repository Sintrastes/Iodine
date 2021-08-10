package com.bedelln.iodine.components

import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.material.Slider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Slider<C: IodineContext>(): ComponentDescription<C, Void, Void, Float, Float> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Float): Component<Void, Void, Float, Float> {
        return object: Component<Void,Void,Float,Float> {

            private val contentsFlow = MutableStateFlow(initialValue)

            @Composable
            override fun contents() {
                var sliderPosition by remember { mutableStateOf(0f) }
                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        ctx.defaultScope.launch {
                            contentsFlow.emit(it)
                        }
                    }
                )
            }

            override fun onEvent(event: Void) { }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Float>
                get() = contentsFlow
        }
    }
}