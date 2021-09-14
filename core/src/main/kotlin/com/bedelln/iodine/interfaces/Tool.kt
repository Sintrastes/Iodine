package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import arrow.continuations.Effect

typealias ToolDescription<C,A,B>
        = Description<C, A, Tool<C, B>>

interface Tool<in C,out A> {
    suspend fun runTool(ctx: C): A

    companion object {
        fun <C,A> noop() = create<C,A,Unit> { }
        fun <C,A,B> create(f: suspend C.(A) -> B): ToolDescription<C, A, B> {
            return object: ToolDescription<C, A, B> {
                @Composable
                override fun initCompose(ctx: C) { }

                override fun initialize(ctx: C, initialValue: A): Tool<C, B> {
                    return object: Tool<C, B> {
                        override suspend fun runTool(ctx: C): B {
                            return f(ctx, initialValue)
                        }
                    }
                }
            }
        }
        fun <C,A,B> just(value: B): ToolDescription<C, A, B> {
            return create { it: A ->
                value
            }
        }

        fun interface ToolEffect<C, A>: Effect<ToolDescription<C, Unit, A>> {
            suspend fun ToolDescription<C, Unit, A>.bind(): A {
                return control().shift(this)
            }
        }

        operator fun <C, A> invoke(func: suspend ToolEffect<C, *>.() -> A): ToolDescription<C, Unit, A> =
            Effect.restricted(
                eff = { ToolEffect { it } },
                f = func,
                just = { just(it) }
            )
    }
}

inline fun <C,A,B,X> ToolDescription<C, A, B>.lmap(crossinline f: (X) -> A): ToolDescription<C, X, B> {
    val origDescr = this
    return object: ToolDescription<C, X, B> {
        override fun initialize(ctx: C, initialValue: X): Tool<C, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Tool<C, B> {
                override suspend fun runTool(ctx: C): B {
                    return orig.runTool(ctx)
                }
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

inline fun <C,A,B,X> ToolDescription<C, A, B>.rmap(crossinline f: suspend (B) -> X): ToolDescription<C, A, X> {
    val origDescr = this
    return object: ToolDescription<C, A, X> {
        override fun initialize(ctx: C, initialValue: A): Tool<C, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Tool<C, X> {
                override suspend fun runTool(ctx: C): X {
                    return f(orig.runTool(ctx))
                }
            }
        }
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

fun <Ctx,A,B,C> ToolDescription<Ctx, A, B>.compose(
    other: ToolDescription<Ctx, B, C>
): ToolDescription<Ctx, A, C> {
    val thisToolDescr = this
    return object: ToolDescription<Ctx, A, C> {
        @Composable
        override fun initCompose(ctx: Ctx) {
            thisToolDescr.initCompose(ctx)
            other.initCompose(ctx)
        }

        override fun initialize(ctx: Ctx, initialValue: A): Tool<Ctx, C> {
            val thisTool = thisToolDescr.initialize(ctx, initialValue)
            val composedTool: suspend () -> Tool<Ctx, C> = {
                val res = thisTool.runTool(ctx)
                other.initialize(ctx, res)
            }
            return object: Tool<Ctx, C> {
                override suspend fun runTool(ctx: Ctx): C {
                    return composedTool().runTool(ctx)
                }
            }
        }
    }
}

inline fun <Ctx,A,B> ToolDescription<Ctx, Unit, A>.thenTool(
    crossinline f: (A) -> ToolDescription<Ctx, Unit, B>
) = run {
    val origTool = this
    object : ToolDescription<Ctx, Unit, B> {

        lateinit var secondTool: ToolDescription<Ctx, Unit, B>

        @Composable
        override fun initCompose(ctx: Ctx) {
            var initializedSecondTool by remember { mutableStateOf(false) }
            origTool.initCompose(ctx)
            if (initializedSecondTool) {
                secondTool.initCompose(ctx)
            }
        }

        override fun initialize(ctx: Ctx, initialValue: Unit): Tool<Ctx, B> {
            val tool = origTool.initialize(ctx,initialValue)
            return object: Tool<Ctx, B> {
                override suspend fun runTool(ctx: Ctx): B {
                    val res = tool.runTool(ctx)
                    secondTool = f(res)
                    return secondTool.initialize(ctx, Unit)
                        .runTool(ctx)
                }
            }
        }
    }
}