package com.bedelln.iodine.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bedelln.iodine.interfaces.Tool
import com.bedelln.iodine.interfaces.ToolState
import kotlinx.coroutines.flow.StateFlow

fun <A> Noop() = object : Tool<A> {
    @Composable
    override fun UI(): ToolState<A> = ToolState.Initial

    override fun start() {}
}

/** Helper function to build dynamic UIs. Takes a StateFlow of
 * composables, and updated the UI as the state flow changes. */
@Composable
fun Dynamic(contentsFlow: StateFlow<@Composable () -> Unit>) {
    val contentsState = contentsFlow.collectAsState()
    val contents by remember { contentsState }

    println("Rendering new contents: ${contents.hashCode()}")
    contents()
}