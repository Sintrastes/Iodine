package com.bedelln.composetk.components

import androidx.compose.foundation.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import com.bedelln.composetk.Component
import com.bedelln.composetk.ComponentDescription
import com.bedelln.composetk.desktop.WindowCtx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

object TextEntry : ComponentDescription<WindowCtx, Void, String, String> {
    @Composable
    override fun initialize(ctx: WindowCtx, initialValue: String): Component<Void, String, String> {
        return object : Component<Void, String, String> {
            private val contentsFlow = MutableStateFlow(initialValue)
            private var contents by remember { mutableStateOf(initialValue) }

            @Composable
            override fun contents() {
                TextField(
                    value = contents,
                    onValueChange = { newValue ->
                        contents = newValue
                        ctx.coroutineScope.launch {
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