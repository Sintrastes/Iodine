package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/********************* Basic interfaces **********************/

interface FormViewModel<out I, out E, in A, out B>
    : Settable<A>, HasEvents<E>, Gettable<B>, HasImpl<I>

interface Form<out I, out E, in A, out B> : FormViewModel<I, E, A, B>, HasContents {
    @Composable
    fun result(): B {
        val state = result.collectAsState()
        val res by remember { state }
        return res
    }
}

/************************ Type aliases ********************/

interface FormImpl<I, E, S, A, B> : ComponentImpl<I, E, S, A>, Form<I, E, A, B>

typealias SFormImpl<I, E, S, A>
        = FormImpl<I, E, S, A, A>

typealias SForm<I, E, A>
        = Form<I, E, A, A>

typealias FormImplDescription<Ctx, I, E, S, A, B>
        = Description<Ctx, A, FormImpl<I, E, S, A, B>>

typealias PFormDescription<Ctx, I, E, P, A, B>
        = Description<Ctx, P, Form<I, E, A, B>>

typealias FormDescription<Ctx, I, E, A, B>
        = PFormDescription<Ctx, I, E, A, A, B>

typealias SFormDescription<Ctx, I, E, A>
        = Description<Ctx, A, Form<I, E, A, A>>

/************************* Core utilities ******************************/

/**
 * Convert a [Form] into a [Component], ignoring the additional structure of the form.
 *
 * This is an explicit cast rather than a subtyping relationship to avoid
 *  ambiguity between utility functions of the same name operating on forms
 *  and components.
 */
fun <I,E,A,B> Form<I,E,A,B>.asComponent(): Component<I,E,A> {
    return object:
        HasContents by this,
        HasImpl<I> by this,
        HasEvents<E> by this,
        Settable<A> by this,
        Component<I,E,A> {}
}

inline fun <C : IodineContext, I, E, P, A, B, X> PFormDescription<C, I, E, P, A, B>.omap(
    crossinline f: (B) -> X
): PFormDescription<C, I, E, P, A, X> {
    val origDescr = this
    return object : PFormDescription<C, I, E, P, A, X> {
        override fun initialize(ctx: C, initialValue: P): Form<I, E, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object : Form<I, E, A, X> {
                @Composable
                override fun contents() {
                    orig.contents()
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

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
): FormDescription<C, I, E, X, B> {
    val origDescr = this
    return object : FormDescription<C, I, E, X, B> {
        override fun initialize(ctx: C, initialValue: X): Form<I, E, X, B> {
            val orig = origDescr.initialize(ctx, from(initialValue))
            return object : Form<I, E, X, B> {
                override fun setValue(newValue: X) {
                    orig.setValue(from(newValue))
                }

                @Composable
                override fun contents() {
                    orig.contents()
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
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

inline fun <C : IodineContext, I, E, P, A, B, X> PFormDescription<C, I, E, P, A, B>.imapParameter(
    crossinline from: (X) -> P
): PFormDescription<C, I, E, X, A, B> {
    val origDescr = this
    return object : PFormDescription<C, I, E, X, A, B> {
        override fun initialize(ctx: C, initialValue: X): Form<I, E, A, B> {
            val orig = origDescr.initialize(ctx, from(initialValue))
            return object : Form<I, E, A, B> {
                @Composable
                override fun contents() {
                    orig.contents()
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
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

/** Supply an initial value to a component. */
fun <C : IodineContext, I, E, P, A, B> PFormDescription<C, I, E, P, A, B>.initialValue(
    x: P
): PFormDescription<C, I, E, Unit, A, B> = this.imapParameter { x }

/*
 * Helper function for making a form from a composable function from
 * the state of the type of values being entered by the form.
 */
inline fun <C : IodineContext, A> ComposeForm(
    crossinline f: @Composable() C.(A) -> Unit
): SFormDescription<C, Unit, Void, A> {
    return object : SFormDescription<C, Unit, Void, A> {

        override fun initialize(ctx: C, initialValue: A): SFormImpl<Unit, Void, A, A> {
            return object : SFormImpl<Unit, Void, A, A> {
                val contentsFlow = MutableStateFlow(initialValue)

                @Composable
                override fun contents(state: A) {
                    ctx.f(state)
                }

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