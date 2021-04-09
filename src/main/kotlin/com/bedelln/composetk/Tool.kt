package com.bedelln.composetk

import androidx.compose.foundation.Text
import arrow.continuations.Effect
import com.bedelln.composetk.desktop.WindowCtx
import com.bedelln.composetk.tools.AlertDialog

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

fun interface ToolEffect<C,A>: Effect<Tool<C, A>> {
    suspend fun Tool<C,A>.bind(): A =
        control().shift(this)
}

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