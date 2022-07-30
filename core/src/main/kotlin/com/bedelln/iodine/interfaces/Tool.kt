package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import arrow.core.Either
import arrow.core.Option
import com.bedelln.iodine.tools.Dynamic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

sealed interface ToolState<out A> {
    object Cancelled : ToolState<Nothing>
    object Initial : ToolState<Nothing>
    data class Returned<out A>(val value: A) : ToolState<A>
}

interface Tool<A> {
    // Idea: Build up the UI for the tool, and then
    // return a result when the user hits submit.
    @Composable
    fun UI(): ToolState<A>

    // Function to "open" the tool in the UI
    fun start()
}

interface OptionTool<A> {
    // Idea: Build up the UI for the tool, and then
    // return a result when the user hits submit.
    @Composable
    fun UI(): Option<A>?

    // Function to "open" the tool in the UI
    fun start()
}

interface EitherTool<E, A> {
    // Idea: Build up the UI for the tool, and then
    // return a result when the user hits submit.
    @Composable
    fun UI(): Either<E, A>?

    // Function to "open" the tool in the UI
    fun start()
}

abstract class ToolBuilder<A>(
    val scope: CoroutineScope,
    val currentContents: MutableStateFlow<@Composable () -> Unit>
) : Effect<A?> {
    suspend fun <B> Tool<B>.bind(): B {
        val toolResult = MutableSharedFlow<B?>()

        scope.launch {
            val newComposable = @Composable {
                val tool = this@bind
                val result = tool.UI()

                println("Result for ${tool.hashCode()}: $result")
                remember { scope.launch { tool.start() } }

                when (result) {
                    is ToolState.Returned -> remember {
                        scope.launch {
                            println("Launching result: ${toolResult.hashCode()}")
                            toolResult.emit(
                                result.value
                            )
                        }
                    }
                    ToolState.Cancelled -> remember {
                        scope.launch {
                            println("Launching result: null")
                            toolResult.emit(
                                null
                            )
                        }
                    }
                    ToolState.Initial -> { }
                }

                Unit
            }

            currentContents.emit(newComposable)
        }

        return toolResult.first()
            ?: control().shift(null)
    }
}

fun <A> CoroutineScope.tool(handler: suspend ToolBuilder<*>.() -> A?): Tool<A> = object : Tool<A> {
    var scope = this@tool + Job()

    val currentContents = MutableStateFlow<@Composable () -> Unit> { }

    val finalResultFlow = MutableStateFlow<ToolState<A>>(
        ToolState.Initial
    )

    @Composable
    override fun UI(): ToolState<A> {
        val finalResultState = finalResultFlow.collectAsState()
        val finalResult by remember { finalResultState }

        Dynamic(currentContents)

        return finalResult
    }

    override fun start() {
        launch {
            val finalResult = Effect.suspended(
                eff = {
                    object: ToolBuilder<A?>(scope, currentContents) {
                        override fun control(): DelimitedScope<A?> {
                            return it
                        }
                    }
                },
                f = handler,
                just = { it }
            )

            finalResultFlow.emit(
                if (finalResult != null) {
                    ToolState.Returned(
                        finalResult
                    )
                } else {
                    ToolState.Cancelled
                }
            )

            // Reset the coroutine scope
            // for this workflow
            scope.cancel()
            scope = this@tool + Job()

            currentContents.emit { }
        }
    }
}
