package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import arrow.continuations.Effect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.function.Consumer
import kotlin.coroutines.Continuation

typealias ToolDescription<C,E,A,B>
    = Description<C, A, Tool<C, E, B>>

interface Tool<in C: IodineContext,out E,out A> {
    val events: Flow<E>

    suspend fun runTool(ctx: C): A

    companion object {
        fun <C: IodineContext,E,A> noop() = create<C,E,A,Unit> { _, _ -> }
        fun <C: IodineContext,E,A,B> create(f: suspend C.(Consumer<E>, A) -> B): ToolDescription<C, E, A, B> {
            return object: ToolDescription<C, E, A, B> {
                @Composable
                override fun initCompose(ctx: C) { }

                override fun initialize(ctx: C, initialValue: A): Tool<C, E, B> {
                    return object: Tool<C, E, B> {
                        override suspend fun runTool(ctx: C): B {
                            return f(ctx, { ctx.defaultScope.launch { mutEvents.emit(it) } },initialValue)
                        }

                        val mutEvents = MutableSharedFlow<E>()

                        override val events: Flow<E>
                            get() = mutEvents
                    }
                }
            }
        }
        fun <C: IodineContext,E,A,B> just(value: B): ToolDescription<C, E, A, B> {
            return create { _, _: A ->
                value
            }
        }

        fun interface ToolEffect<C: IodineContext, E, A>: Effect<ToolDescription<C, E, Unit, A>> {
            suspend fun ToolDescription<C, E, Unit, A>.bind(): A {
                return control().shift(this)
            }
        }

        operator fun <C: IodineContext, E, A> invoke(func: suspend ToolEffect<C, E, *>.() -> A): ToolDescription<C, E, Unit, A> =
            Effect.restricted(
                eff = { ToolEffect { it } },
                f = func,
                just = { just(it) }
            )
    }
}

inline fun <C: IodineContext,E,A,B,X> ToolDescription<C, E, A, B>.lmap(crossinline f: (X) -> A): ToolDescription<C, E, X, B> {
    val origDescr = this
    return object: ToolDescription<C, E, X, B> {
        override fun initialize(ctx: C, initialValue: X): Tool<C, E, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Tool<C, E, B> {
                override suspend fun runTool(ctx: C): B {
                    return orig.runTool(ctx)
                }

                override val events: Flow<E>
                    get() = orig.events
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

inline fun <C: IodineContext,E,A,B,X> ToolDescription<C, E, A, B>.rmap(crossinline f: suspend (B) -> X): ToolDescription<C, E, A, X> {
    val origDescr = this
    return object: ToolDescription<C, E, A, X> {
        override fun initialize(ctx: C, initialValue: A): Tool<C, E, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Tool<C, E, X> {
                override suspend fun runTool(ctx: C): X {
                    return f(orig.runTool(ctx))
                }

                override val events: Flow<E>
                    get() = orig.events
            }
        }
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

fun <Ctx: IodineContext,E,A,B,C> ToolDescription<Ctx, E, A, B>.compose(
    other: ToolDescription<Ctx, E, B, C>
): ToolDescription<Ctx, E, A, C> {
    val thisToolDescr = this
    return object: ToolDescription<Ctx, E, A, C> {
        @Composable
        override fun initCompose(ctx: Ctx) {
            thisToolDescr.initCompose(ctx)
            other.initCompose(ctx)
        }

        override fun initialize(ctx: Ctx, initialValue: A): Tool<Ctx, E, C> {
            val thisTool = thisToolDescr.initialize(ctx, initialValue)
            val composedTool: suspend () -> Tool<Ctx, E, C> = {
                val res = thisTool.runTool(ctx)
                other.initialize(ctx, res)
            }
            return object: Tool<Ctx, E, C> {
                override suspend fun runTool(ctx: Ctx): C {
                    return composedTool().runTool(ctx)
                }

                // TODO: Include the events of the other tool.
                override val events: Flow<E>
                    get() = thisTool.events
            }
        }
    }
}

inline fun <Ctx: IodineContext,E,A,B> ToolDescription<Ctx, E, Unit, A>.thenTool(
    crossinline f: (A) -> ToolDescription<Ctx, E, Unit, B>
) = run {
    val origTool = this
    object : ToolDescription<Ctx, E, Unit, B> {

        lateinit var secondTool: ToolDescription<Ctx, E, Unit, B>

        @Composable
        override fun initCompose(ctx: Ctx) {
            var initializedSecondTool by remember { mutableStateOf(false) }
            origTool.initCompose(ctx)
            if (initializedSecondTool) {
                secondTool.initCompose(ctx)
            }
        }

        override fun initialize(ctx: Ctx, initialValue: Unit): Tool<Ctx, E, B> {
            val tool = origTool.initialize(ctx,initialValue)
            return object: Tool<Ctx, E, B> {
                override suspend fun runTool(ctx: Ctx): B {
                    val res = tool.runTool(ctx)
                    secondTool = f(res)
                    return secondTool.initialize(ctx, Unit)
                        .runTool(ctx)
                }

                // TODO: Include the events of the other tool as well.
                override val events: Flow<E>
                    get() = tool.events
            }
        }
    }
}