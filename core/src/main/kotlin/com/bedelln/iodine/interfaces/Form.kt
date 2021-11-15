package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

interface FormViewModel<out I, out E, A, out B> : ViewModel<I, E, A, A>, Gettable<B>

interface Form<out I, out E, A, out B> : ComponentImpl<I, E, A, A>, FormViewModel<I, E, A, B>

typealias SForm<I, E, A>
    = Form<I, E, A, A>

typealias FormDescription<Ctx, I, E, A, B>
    = Description<Ctx, A, Form<I, E, A, B>>

typealias SFormDescription<Ctx, I, E, A>
    = Description<Ctx, A, Form<I, E, A, A>>

inline fun <C : IodineContext, I, E, A, B, X> FormDescription<C, I, E, A, B>.omap(
    crossinline f: (B) -> X
): FormDescription<C, I, E, A, X> {
    val origDescr = this
    return object : FormDescription<C, I, E, A, X> {
        override fun initialize(ctx: C, initialValue: A): Form<I, E, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object : Form<I, E, A, X> {
                @Composable
                override fun contents(state: A) {
                    orig.contents(state)
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

                override val state: StateFlow<A>
                    get() = orig.state
                override val impl: I
                    get() = orig.impl
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

inline fun <C : IodineContext, I, E, S, A, B, X> FormDescription<C, I, E, A, B>.imap(
    crossinline from: (X) -> A,
    crossinline to: (A) -> X
): FormDescription<C, I, E, X, B> {
    val origDescr = this
    return object : FormDescription<C, I, E, X, B> {
        override fun initialize(ctx: C, initialValue: X): Form<I, E, X, B> {
            val orig = origDescr.initialize(ctx, from(initialValue))
            return object : Form<I, E, X, B> {
                @Composable
                override fun contents(state: X) {
                    orig.contents(from(state))
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result

                override val state: StateFlow<X>
                    get() = orig.state
                        .mapStateFlow(to)
                override val impl: I
                    get() = orig.impl
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

/**
 * Helper function for making a form from a composable function from
 * the state of the type of values being entered by the form.
 */
inline fun <C: IodineContext, A> ComposeForm(
    crossinline f: @Composable() C.(A) -> Unit
): SFormDescription<C, Any, Void, A> {
    return object: SFormDescription<C, Any, Void, A> {

        override fun initialize(ctx: C, initialValue: A): SForm<Any, Void, A> {
            return object: SForm<Unit, Void, A> {
                val contentsFlow = MutableStateFlow(initialValue)

                @Composable
                override fun contents(state: A) { ctx.f(state) }

                override val events: Flow<Void>
                    get() = emptyFlow()

                override val impl: Unit
                    get() = Unit
                override val result: StateFlow<A>
                    get() = state
                override val state: StateFlow<A>
                    get() = contentsFlow
            }
        }
    }
}