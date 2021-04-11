package com.bedelln.iodine

import androidx.compose.runtime.Composable
import arrow.continuations.Effect
import com.bedelln.iodine.components.ActionButton
import com.bedelln.iodine.components.TextEntry
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.tools.AlertDialog

interface Description<in C,in A,out B> {
    @Composable
    fun initCompose(ctx: C)

    fun initialize(ctx: C, initialValue: A): B
}

typealias ToolDescription<C,A,B>
        = Description<C,A,Tool<C,B>>

interface Tool<in C,out A> {
    suspend fun runTool(ctx: C): A

    companion object {
        fun <C> noop() = Tool.create<C,Unit> { }
        fun <C,A> create(f: suspend C.() -> A): Tool<C,A> {
            return object: Tool<C,A> {
                override suspend fun runTool(ctx: C): A {
                    return f(ctx)
                }
            }
        }
        fun <C,A,B> just(value: B): ToolDescription<C,A,B> {
            TODO()
        }
    }
}

inline fun <C,A,B,X> ToolDescription<C,A,B>.lmap(crossinline f: (X) -> A): ToolDescription<C,X,B> {
    val origDescr = this
    return object: ToolDescription<C,X,B> {
        override fun initialize(ctx: C, initialValue: X): Tool<C, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Tool<C,B> {
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

inline fun <C,A,B,X> ToolDescription<C,A,B>.rmap(crossinline f: suspend (B) -> X): ToolDescription<C,A,X> {
    val origDescr = this
    return object: ToolDescription<C,A,X> {
        override fun initialize(ctx: C, initialValue: A): Tool<C, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Tool<C,X> {
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

fun <Ctx,A,B,C> ToolDescription<Ctx,A,B>.compose(
    other: ToolDescription<Ctx,B,C>
): ToolDescription<Ctx,A,C> {
    val thisToolDescr = this
    return object: ToolDescription<Ctx,A,C> {
        @Composable
        override fun initCompose(ctx: Ctx) {
            thisToolDescr.initCompose(ctx)
            other.initCompose(ctx)
        }

        override fun initialize(ctx: Ctx, initialValue: A): Tool<Ctx, C> {
            val thisTool = thisToolDescr.initialize(ctx, initialValue)
            val composedTool: suspend () -> Tool<Ctx,C> = {
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

fun interface ToolEffect<C,A>: Effect<ToolDescription<C, Unit, A>> {
    suspend operator fun ToolDescription<C,Unit,A>.not(): A =
        control().shift(this)
}

object tool {
    operator fun <C, A> invoke(func: suspend ToolEffect<C,A>.() -> ToolDescription<C,Unit,A>): ToolDescription<C,Unit,A> =
        Effect.restricted(eff = { ToolEffect { it } }, f = func, just = { it })
}


val test: ToolDescription<WindowCtx, Unit, Unit> = tool {
    val res = !AlertDialog(
        title = "Test",
        contents = ActionButton(
            text = "Hello!",
            action = AlertDialog(
                title = "My alert",
                contents = TextEntry
            )
                .lmap { it: Unit -> "test" }
                .rmap { }
        )
    )
    !AlertDialog(
        title = "Test",
        contents = ActionButton(
            text = "Hello!",
            action = AlertDialog(
                title = "My alert",
                contents = TextEntry
            )
                .lmap { it: Unit -> "test" }
                .rmap { }
        )
    )
    Tool.just(res)
}