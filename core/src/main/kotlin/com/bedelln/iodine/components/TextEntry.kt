package com.bedelln.iodine.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.ComponentImpl
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TextEntry<C: IodineContext>(): ComponentDescription<C, TextEntry.Action, Void, String> {
    interface Action {
        fun setText(value: String)
    }

    override fun initialize(ctx: C, initialValue: String): Component<Action, Void, String> {
        return object : ComponentImpl<Action, Void, String, String> {
            private val contentsFlow = MutableStateFlow(initialValue)
            override val state = contentsFlow

            @Composable
            override fun contents(state: String) {
                TextField(
                    value = state,
                    onValueChange = { newValue ->
                        ctx.defaultScope.launch {
                            contentsFlow.emit(newValue)
                        }
                    },
                    label = { Text("") }
                )
            }

            override val impl = object : Action {
                override fun setText(value: String) {
                    ctx.defaultScope.launch {
                        contentsFlow.emit(value)
                    }
                }
            }
            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}