package com.bedelln.iodine.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.launch

/**
 * An iodine component for
 */
class TextEntry(): SForm<String> {
    @Composable
    override fun contents(initialValue: String): String {
        var state by remember { mutableStateOf(initialValue) }
        TextField(
            value = state,
            onValueChange = { newValue ->
                state = newValue
            },
            label = { Text("") }
        )
        return state
    }
}