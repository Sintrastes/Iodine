package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import java.util.function.Consumer

interface Settable<in C, in A> {
    fun C.setValue(newValue: A)
}

interface Component<in Ei, out Eo, in A, out B> {
    @Composable fun contents()
    fun onEvent(event: Ei)
    val events: Flow<Eo>
    val result: StateFlow<B>
}

interface SettableComponent<C: IodineContext, in Ei, out Eo, in A, out B>: Component<Ei, Eo, A, B>, Settable<C, A>

typealias SettableComponentDescription<C,Ei,Eo,A,B> =
        Description<C, A, SettableComponent<C, Ei, Eo, A, B>>

typealias ComponentDescription<C,Ei,Eo,A,B> =
        Description<C, A, Component<Ei, Eo, A, B>>

/** Helper function for making a component from a composable function. */
inline fun <C: IodineContext> Compose(crossinline f: @Composable() () -> Unit): ComponentDescription<C, Void, Void, Unit, Unit> {
    return object: ComponentDescription<C, Void, Void, Unit, Unit> {
        @Composable
        override fun initCompose(ctx: C) { }

        override fun initialize(ctx: C, initialValue: Unit): Component<Void, Void, Unit, Unit> {
            return object: Component<Void, Void, Unit, Unit> {
                @Composable
                override fun contents() { f() }

                override fun onEvent(event: Void) { }
                override val events: Flow<Void>
                    get() = emptyFlow()
                override val result: StateFlow<Unit>
                    get() = MutableStateFlow(Unit)
            }
        }
    }
}

/** Supply an initial value to a component. */
fun <C,Ei,Eo,A,B> ComponentDescription<C, Ei, Eo, A, B>.initialValue(
    x: A
): ComponentDescription<C, Ei, Eo, Unit, B>
 = this.imap { it: Unit -> x }

inline fun <C,Ei,Eo,A,B,X> ComponentDescription<C, Ei, Eo, A, B>.imap(
    crossinline f: (X) -> A
): ComponentDescription<C, Ei, Eo, X, B> {
    val origDescr = this
    return object: ComponentDescription<C, Ei, Eo, X, B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: X): Component<Ei, Eo, X, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: Component<Ei, Eo, X, B> {
                @Composable
                override fun contents() {
                    orig.contents()
                }
                override fun onEvent(event: Ei) {
                    orig.onEvent(event)
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}

fun <C,Ei,Eo,A,B,X> ComponentDescription<C, Ei, Eo, A, B>.omap(f: (B) -> X): ComponentDescription<C, Ei, Eo, A, X> {
    val origDescr = this
    return object: ComponentDescription<C, Ei, Eo, A, X> {
        override fun initialize(ctx: C, initialValue: A): Component<Ei, Eo, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<Ei, Eo, A, X> {
                @Composable
                override fun contents() {
                    orig.contents()
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

                override fun onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

@Composable
fun <Ei,Eo,A,B,C: IodineContext> Component<Ei, Eo, A, B>.getContents(ctx: C) {
    val component = this
    val newEvents = MutableSharedFlow<Eo>()
    component.events
        .onEach { event ->
            newEvents.emit(
                event
            )
        }
    val newComponent = object: Component<Ei, Eo, A, B> {
        @Composable
        override fun contents() {
            with(component) { contents() }
        }

        override fun onEvent(event: Ei) {
            with(component) { onEvent(event) }
        }

        override val events: Flow<Eo>
            get() = newEvents
        override val result: StateFlow<B>
            get() = component.result

    }
    newComponent.contents()
}

inline fun <C,Ei,Eo,A,B> WrappedComponent(crossinline layout: @Composable () (@Composable () () -> Unit) -> Unit, component: ComponentDescription<C, Ei, Eo, A, B>): ComponentDescription<C, Ei, Eo, A, B>
    = component.wrap(layout)

inline fun <C,Ei,Eo,A,B> ComponentDescription<C, Ei, Eo, A, B>.wrap(crossinline f: @Composable () (@Composable () () -> Unit) -> Unit): ComponentDescription<C, Ei, Eo, A, B> {
    val origDescr = this
    return object: ComponentDescription<C, Ei, Eo, A, B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<Ei, Eo, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<Ei, Eo, A, B> {
                @Composable
                override fun contents() {
                    f { with(orig) { contents() } }
                }

                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result

                override fun onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}

inline fun <C,D,Ei,Eo,A,B> ComponentDescription<C, Ei, Eo, A, B>.mapCtx(crossinline  f: (D) -> C): ComponentDescription<D, Ei, Eo, A, B> {
    val origDescr = this
    return object: ComponentDescription<D, Ei, Eo, A, B> {
        @Composable
        override fun initCompose(ctx: D) {
            origDescr.initCompose(f(ctx))
        }

        override fun initialize(ctx: D, initialValue: A): Component<Ei, Eo, A, B> {
            val orig = origDescr.initialize(f(ctx), initialValue)
            return object: Component<Ei, Eo, A, B> {
                @Composable
                override fun contents() {
                    with(orig) { contents() }
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
                override fun onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}

inline fun <C,Ei,Eo,X,A,B> ComponentDescription<C, Ei, Eo, A, B>.mapEvents(crossinline f: suspend (Eo) -> X): ComponentDescription<C, Ei, X, A, B> {
    val origDescr = this
    return object: ComponentDescription<C, Ei, X, A, B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<Ei, X, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<Ei, X, A, B> {
                @Composable
                override fun contents() {
                    with(orig) { contents() }
                }

                override val events: Flow<X>
                    get() = orig.events.map(f)
                override val result: StateFlow<B>
                    get() = orig.result

                override fun onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}

inline fun <C,Ei,Eo,A,B> ComponentDescription<C, Ei, Eo, A, B>.ignoreEvents(
): ComponentDescription<C, Void, Void, A, B> {
    val origDescr = this
    return object: ComponentDescription<C, Void, Void, A, B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<Void, Void, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<Void, Void, A, B> {
                @Composable
                override fun contents() {
                    with(orig) { contents() }
                }

                override val events: Flow<Void>
                    get() = emptyFlow()
                override val result: StateFlow<B>
                    get() = orig.result

                override fun onEvent(event: Void) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}