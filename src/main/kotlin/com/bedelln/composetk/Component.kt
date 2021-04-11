package com.bedelln.composetk

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

interface Settable<in C, in A> {
    fun C.setValue(newValue: A)
}

interface Component<out E,in A,out B> {
    @Composable fun contents()
    val events: Flow<E>
    val result: StateFlow<B>
}

typealias ComponentDescription<C,E,A,B> =
    Description<C,A,Component<E,A,B>>

inline fun <C,E,A,B,X> ComponentDescription<C,E,A,B>.imap(crossinline f: (X) -> A): ComponentDescription<C,E,X,B> {
    val origDescr = this
    return object: ComponentDescription<C,E,X,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: X): Component<E, X, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Component<E,X,B> {
                @Composable
                override fun contents() {
                    orig.contents()
                }
                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}

fun <C,E,A,B,X> ComponentDescription<C,E,A,B>.omap(f: suspend (B) -> X): ComponentDescription<C,E,A,X> {
    val origDescr = this
    return object: ComponentDescription<C,E,A,X> {

        override fun initialize(ctx: C, initialValue: A): Component<E, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<E,A,X> {
                @Composable
                override fun contents() {
                    orig.contents()
                }
                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result.map(f) as StateFlow<X>
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}