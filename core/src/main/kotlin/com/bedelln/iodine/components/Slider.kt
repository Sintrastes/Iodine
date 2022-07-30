package com.bedelln.iodine.components

import androidx.compose.runtime.*
import androidx.compose.material.Slider
import androidx.compose.ui.Modifier
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Slider(
    val scope: CoroutineScope,
    val modifier: Modifier = Modifier
) : Component<Unit, Void, Float> {
    @Composable
    override fun contents(initialValue: Float) {
        var state by remember { mutableStateOf(initialValue) }
        Slider(
            value = state,
            onValueChange = {
                state = it
            }
        )
    }

    override val impl = Unit
}