package com.bedelln.composetk

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import arrow.continuations.Effect
import com.bedelln.composetk.desktop.WindowCtx
import com.bedelln.composetk.tools.AlertDialog

fun interface Description<in C,in A,out B> {
    @Composable
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
    }
}

inline fun <C,A,B,X> ToolDescription<C,A,B>.imap(crossinline f: (X) -> A): ToolDescription<C,X,B> {
    val origDescr = this
    return object: ToolDescription<C,X,B> {
        @Composable
        override fun initialize(ctx: C, initialValue: X): Tool<C, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Tool<C,B> {
                override suspend fun runTool(ctx: C): B {
                    return orig.runTool(ctx)
                }
            }
        }
    }
}

inline fun <C,A,B,X> ToolDescription<C,A,B>.omap(crossinline f: suspend (B) -> X): ToolDescription<C,A,X> {
    val origDescr = this
    return object: ToolDescription<C,A,X> {
        @Composable
        override fun initialize(ctx: C, initialValue: A): Tool<C, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Tool<C,X> {
                override suspend fun runTool(ctx: C): X {
                    return f(orig.runTool(ctx))
                }
            }
        }
    }
}

/*
fun <Ctx,A,B,C> ToolDescription<Ctx,A,B>.compose(
    other: ToolDescription<Ctx,B,C>
): ToolDescription<Ctx,A,C> {
    val thisToolDescr = this
    return object: ToolDescription<Ctx,A,C> {
        @Composable
        override fun initialize(ctx: Ctx, initialValue: A): Tool<Ctx, C> {
            val thisTool = thisToolDescr.initialize(ctx, initialValue)
            // TODO: To make this work, I'll probably need to
            // seperate out initialize to "@Composable initialize(ctx)
            // and initialize(ctx, initialValue: A)
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
 */

fun interface ToolEffect<C,A>: Effect<Tool<C, A>> {
    suspend fun Tool<C,A>.bind(): A =
        control().shift(this)
}

/*
object tool {
    operator fun <C, A> invoke(func: suspend ToolEffect<*,*>.() -> Tool<C,A>): Tool<C,A> =
        Effect.restricted(eff = { ToolEffect { it } }, f = func, just = { it })
}

/*
val test: Tool<WindowCtx, Unit> = tool {
    val res = AlertDialog(title = "Test") {
        Text("Hello!")
    }.bind()
    AlertDialog(title = "Test2") {
        Text("Hello again!")
    }
}
 */