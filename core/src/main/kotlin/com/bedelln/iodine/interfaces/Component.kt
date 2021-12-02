package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

interface Settable<in A> {
    fun setValue(newValue: A) { }
}

interface Gettable<out B> {
    val result: StateFlow<B>
}

interface ViewModel<out I, out E, in A>: Settable<A>, HasEvents<E>, HasImpl<I>

interface ViewModelImpl<out I, out E, S, in A>: ViewModel<I,E,A>, HasState<S>

fun <I, E, A,R> ViewModel<I, E, A>.interact(action: I.() -> R): R {
    return impl.action()
}

interface Component<out I, out E, in A>: ViewModel<I,E,A>, HasContents

interface ComponentImpl<out I, out E, S, in A>: ViewModelImpl<I,E,S,A>, Component<I,E,A> {
    @Composable fun contents(state: S)

    @Composable
    override fun contents() {
        val componentState = state.collectAsState()
        val st by remember { componentState }
        contents(st)
    }
}

typealias ComponentDescriptionImpl<Ctx,I,E,S,A> =
        Description<Ctx, A, ComponentImpl<I, E, S, A>>

typealias ComponentDescription<Ctx,I,E,A> =
        PComponentDescription<Ctx,I,E,A,A>

typealias PComponentDescription<Ctx,I,E,P,A> =
        Description<Ctx, P, Component<I, E, A>>

/** Helper function for making a component from a composable function. */
inline fun <C: IodineContext> Compose(
    crossinline f: @Composable() C.() -> Unit
): ComponentDescription<C, Unit, Void, Unit> {
    return object: ComponentDescription<C, Unit, Void, Unit> {
        @Composable
        override fun initCompose(ctx: C) { }

        override fun initialize(ctx: C, initialValue: Unit): ComponentImpl<Unit, Void, Unit, Unit> {
            return object: ComponentImpl<Unit, Void, Unit, Unit> {
                @Composable
                override fun contents(state: Unit) { ctx.f() }

                override val events: Flow<Void>
                    get() = emptyFlow()
                override val state: StateFlow<Unit>
                    get() = MutableStateFlow(Unit)

                override val impl: Unit
                    get() = Unit
            }
        }
    }
}

/** Supply an initial value to a component. */
fun <C,I,E,A> ComponentDescription<C, I, E, A>.initialValue(
    x: A
): ComponentDescription<C, I, E, Unit>
 = this.imap { x }

inline fun <C,I,E,A,X> ComponentDescription<C, I, E, A>.imap(
    crossinline f: (X) -> A
): ComponentDescription<C, I, E, X> {
    val origDescr = this
    return object: ComponentDescription<C, I, E, X> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: X): Component<I, E, X> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Component<I, E, X> {
                @Composable
                override fun contents() {
                    orig.contents()
                }
                override val events: Flow<E>
                    get() = orig.events
                override val impl: I
                    get() = orig.impl
            }
        }
    }
}

/**
 * Helper function to obtain the composable function for rendering the given component description.
 *
 * Useful for making use of preview functionality for composable functions.
 */
@Composable
fun <C: IodineContext, I, E, P, A> PComponentDescription<C, I, E, P, A>.getContents(
    ctx: C,
    initialValue: P
) {
    val description = this
    val component: Component<I, E, A> = remember {
        description.initialize(ctx, initialValue)
    }
    this.initCompose(ctx)

    component.contents()
}

inline fun <C,I,E,A> WrappedComponent(
    crossinline layout: @Composable () (@Composable () () -> Unit) -> Unit,
    component: ComponentDescription<C, I, E, A>
): ComponentDescription<C, I, E, A>
    = component.wrap(layout)

inline fun <C,I,E,A> ComponentDescription<C, I, E, A>.wrap(
    crossinline f: @Composable () (@Composable () () -> Unit) -> Unit
): ComponentDescription<C, I, E, A> {
    val origDescr = this
    return object: ComponentDescription<C, I, E, A> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<I, E, A> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<I, E, A> {
                @Composable
                override fun contents() {
                    f { with(orig) { contents() } }
                }

                override val impl: I
                    get() = orig.impl
                override val events: Flow<E>
                    get() = orig.events
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
inline fun <C,I,E,X,A> ComponentDescription<C, I, E, A>.mapEvents(
    crossinline f: suspend (E) -> X
): ComponentDescription<C, I, X, A> {
    val origDescr = this
    return object: ComponentDescription<C, I, X, A> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<I, X, A> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<I, X, A> {
                @Composable
                override fun contents() {
                    with(orig) { contents() }
                }

                override val events: Flow<X>
                    get() = orig.events.map(f)
                override val impl: I
                    get() = orig.impl
            }
        }
    }
}

// Note: This could be simplified with
// context receivers.
/**
 * Helper function to preform some action on a particular event (or subtype of events) [Ev]
 *  of a component.
 */
inline fun <I, E: Any, reified Ev: E> Component<I, E, Unit>.on(
    ctx: IodineContext,
    event: KClass<Ev>,
    crossinline action: I.() -> Unit
) {
    val component = this
    ctx.defaultScope.launch {
        component.events.collect { event ->
            if (event is Ev)
                component.impl.action()
        }
    }
}

/**
 * Helper function to preform some action on a particular event (or subtype of events) [Ev]
 *  of a component.
 */
inline fun <I, E, A, B> Form<I, E, A, B>.onValueChange(
    ctx: IodineContext,
    crossinline action: (B) -> Unit
) {
    val component = this
    ctx.defaultScope.launch {
        component.result.drop(1).collect {
            action(it)
        }
    }
}