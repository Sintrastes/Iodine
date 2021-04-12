package com.bedelln.iodine.components

import androidx.compose.foundation.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import com.bedelln.iodine.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TextEntry<C: IodineContext>(): ComponentDescription<C, Void, String, String> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: String): Component<Void, String, String> {
        return object : Component<Void, String, String> {
            private val contentsFlow = MutableStateFlow(initialValue)

            @Composable
            override fun contents() {
                var contents by remember { mutableStateOf(initialValue) }
                TextField(
                    value = contents,
                    onValueChange = { newValue ->
                        contents = newValue
                        ctx.defaultScope.launch {
                            contentsFlow.emit(newValue)
                        }
                    },
                    label = { Text("") }
                )
            }

            override val events: Flow<Void> get() = emptyFlow()
            override val result: StateFlow<String>
                get() = contentsFlow
        }
    }
}