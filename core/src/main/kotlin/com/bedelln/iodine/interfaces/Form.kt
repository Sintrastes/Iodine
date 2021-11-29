package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface FormViewModel<out I, out E, S, in A, out B> : ViewModel<I, E, S, A>, Gettable<B>

interface FormImpl<out I, out E, S, in A, out B> : ComponentImpl<I, E, S, A>, FormViewModel<I, E, S, A, B> {
    @Composable
    fun result(): B {
        val state = result.collectAsState()
        val res by remember { state }
        return res
    }
}

typealias Form<I,E,A,B> = FormImpl<I,E,*,A,B>

typealias SFormImpl<I, E, S, A>
    = FormImpl<I, E, S, A, A>

typealias SForm<I, E, A>
    = Form<I, E, A, A>

typealias FormImplDescription<Ctx, I, E, S, A, B>
    = Description<Ctx, A, FormImpl<I, E, S, A, B>>

typealias FormDescription<Ctx, I, E, A, B>
    = Description<Ctx, A, Form<I, E, A, B>>

typealias SFormDescription<Ctx, I, E, A>
    = Description<Ctx, A, Form<I, E, A, A>>

inline fun <C : IodineContext, I, E, S, A, B, X> FormImplDescription<C, I, E, S, A, B>.omap(
    crossinline f: (B) -> X
): FormImplDescription<C, I, E, S, A, X> {
    val origDescr = this
    return object : FormImplDescription<C, I, E, S, A, X> {
        override fun initialize(ctx: C, initialValue: A): FormImpl<I, E, S, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object : FormImpl<I, E, S, A, X> {
                @Composable
                override fun contents(state: S) {
                    orig.contents(state)
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

                override val state: StateFlow<S>
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

inline fun <C : IodineContext, I, E, S, A, B, X> FormImplDescription<C, I, E, S, A, B>.imapImpl(
    crossinline from: (X) -> A
): FormImplDescription<C, I, E, S, X, B> {
    val origDescr = this
    return object : FormImplDescription<C, I, E, S, X, B> {
        override fun initialize(ctx: C, initialValue: X): FormImpl<I, E, S, X, B> {
            val orig = origDescr.initialize(ctx, from(initialValue))
            return object : FormImpl<I, E, S, X, B> {
                @Composable
                override fun contents(state: S) {
                    orig.contents(state)
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result

                override val state: StateFlow<S>
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

inline fun <C : IodineContext, I, E, A, B, X> FormDescription<C, I, E, A, B>.imap(
    crossinline from: (X) -> A
): FormDescription<C, I, E, X, B> =
    (this as FormImplDescription<C,I,E,Any?,A,B>)
        .imapImpl(from)

/*
 * Helper function for making a form from a composable function from
 * the state of the type of values being entered by the form.
 */
inline fun <C: IodineContext, A> ComposeForm(
    crossinline f: @Composable() C.(A) -> Unit
): SFormDescription<C, Unit, Void, A> {
    return object: SFormDescription<C, Unit, Void, A> {

        override fun initialize(ctx: C, initialValue: A): SFormImpl<Unit, Void, A, A> {
            return object: SFormImpl<Unit, Void, A, A> {
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