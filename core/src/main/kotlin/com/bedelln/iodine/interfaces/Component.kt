package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*

interface Settable<in A> {
    fun setValue(newValue: A) { }
}

interface Gettable<out B> {
    val result: StateFlow<B>
}

interface ViewModel<out I, out E, S, in A>: Settable<A> {
    val impl: I
    val events: Flow<E>
    val state: StateFlow<S>
}

fun <I, S, E, A,R> ViewModel<I, S, E, A>.interact(action: I.() -> R): R {
    return impl.action()
}

interface ComponentImpl<out Ctx, out I, out E, S, in A>: ViewModel<I,E,S,A> {
    @Composable fun contents(state: S)
}

typealias Component<Ctx,I,E,A> = ComponentImpl<Ctx,I,E,*,A>

typealias ComponentDescriptionImpl<Ctx,I,E,S,A> =
        Description<Ctx, A, ComponentImpl<Ctx, I, E, S, A>>

typealias ComponentDescription<Ctx,I,E,A> =
        ComponentDescriptionImpl<Ctx,I,E,*,A>

/** Helper function for making a component from a composable function. */
inline fun <C: IodineContext> Compose(
    crossinline f: @Composable() C.() -> Unit
): ComponentDescription<C, Unit, Void, Unit> {
    return object: ComponentDescriptionImpl<C, Unit, Void, Unit, Unit> {
        @Composable
        override fun initCompose(ctx: C) { }

        override fun initialize(ctx: C, initialValue: Unit): ComponentImpl<C, Unit, Void, Unit, Unit> {
            return object: ComponentImpl<C, Unit, Void, Unit, Unit> {
                @Composable
                override fun contents(state: Unit) { ctx.f() }

                override val events: Flow<Void>
                    get() = emptyFlow()
                override val state: StateFlow<Unit>
                    get() = MutableStateFlow(Unit)

                override val impl: Unit
                    get() = TODO("Not yet implemented")
            }
        }
    }
}

/** Supply an initial value to a component. */
fun <C,I,E,S,A> ComponentDescriptionImpl<C, I, E, S, A>.initialValue(
    x: A
): ComponentDescriptionImpl<C, I, E, S, Unit>
 = this.imap { x }

inline fun <C,I,E,S,A,X> ComponentDescriptionImpl<C, I, E, S, A>.imap(
    crossinline f: (X) -> A
): ComponentDescriptionImpl<C, I, E, S, X> {
    val origDescr = this
    return object: ComponentDescriptionImpl<C, I, E, S, X> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: X): ComponentImpl<C, I, E, S, X> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: ComponentImpl<C, I, E, S, X> {
                @Composable
                override fun contents(state: S) {
                    orig.contents(state)
                }
                override val events: Flow<E>
                    get() = orig.events
                override val state: StateFlow<S>
                    get() = orig.state
                override val impl: I
                    get() = orig.impl
            }
        }
    }
}

/**
 * Helper function to obtain the composable function for rendering the given component.
 *
 * Useful for making use of preview functionality for composable functions.
 */
@Composable
fun <C: IodineContext, I, E, S, A> ComponentImpl<C, I, E, S, A>.getContents(ctx: C) {
    val component = this
    val componentState = component.state.collectAsState()
    val state by remember { componentState }
    component.contents(state)
}

/**
 * Helper function to obtain the composable function for rendering the given component description.
 *
 * Useful for making use of preview functionality for composable functions.
 */
@Composable
fun <C: IodineContext, I, E, S, A> ComponentDescriptionImpl<C, I, E, S, A>.getContents(
    ctx: C,
    initialValue: A
) {
    val component = this.initialize(ctx, initialValue)
    this.initCompose(ctx)

    val componentState = component.state.collectAsState()
    val state by remember { componentState }
    component.contents(state)
}

inline fun <C,I,E,S,A> WrappedComponent(
    crossinline layout: @Composable () (@Composable () () -> Unit) -> Unit,
    component: ComponentDescriptionImpl<C, I, E, S, A>
): ComponentDescriptionImpl<C, I, E, S, A>
    = component.wrap(layout)

inline fun <C,I,E,S,A> ComponentDescriptionImpl<C, I, E, S, A>.wrap(
    crossinline f: @Composable () (@Composable () () -> Unit) -> Unit
): ComponentDescriptionImpl<C, I, E, S, A> {
    val origDescr = this
    return object: ComponentDescriptionImpl<C, I, E, S, A> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): ComponentImpl<C, I, E, S, A> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: ComponentImpl<C, I, E, S, A> {
                @Composable
                override fun contents(state: S) {
                    f { with(orig) { contents(state) } }
                }

                override val impl: I
                    get() = orig.impl
                override val events: Flow<E>
                    get() = orig.events
                override val state: StateFlow<S>
                    get() = orig.state
            }
        }
    }
}

/*
// Note: I'm not sure how useful this will be anymore.
// Is there an instance where a transformation like this makes sense?
inline fun <C,D,I,E,A> ComponentDescription<C, I, E, A>.mapCtx(
    crossinline f: (C) -> D
): ComponentDescription<D, I, E, A> {
    val origDescr = this
    return object: ComponentDescription<D, I, E, A> {
        @Composable
        override fun initCompose(ctx: D) {
            origDescr.initCompose(f(ctx))
        }

        override fun initialize(ctx: D, initialValue: A): Component<D, I, E, A> {
            val orig = origDescr.initialize(f(ctx), initialValue)
            return object: Component<D, I, E, A> {
                override fun <A> interact(action: I.(D) -> A): A {
                    return with(orig) { interact { action(f(it)) } }
                }

                @Composable
                override fun contents() {
                    with(orig) { contents() }
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}
 */

/**
 * Helper function for transforming the type of events that a component emits.
 */
inline fun <C,I,E,X,S,A> ComponentDescriptionImpl<C, I, E, S, A>.mapEvents(
    crossinline f: suspend (E) -> X
): ComponentDescriptionImpl<C, I, X, S, A> {
    val origDescr = this
    return object: ComponentDescriptionImpl<C, I, X, S, A> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): ComponentImpl<C, I, X, S, A> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: ComponentImpl<C, I, X, S, A> {
                @Composable
                override fun contents(state: S) {
                    with(orig) { contents(state) }
                }

                override val events: Flow<X>
                    get() = orig.events.map(f)
                override val impl: I
                    get() = orig.impl
                override val state: StateFlow<S>
                    get() = orig.state
            }
        }
    }
}