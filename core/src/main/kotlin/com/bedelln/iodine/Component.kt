package com.bedelln.iodine

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.launch

interface Settable<in C, in A> {
    fun C.setValue(newValue: A)
}

interface ComponentAction<A, Eo> {
    fun emit(event: Eo)
    fun setValue(value: A)
}

inline fun <X,A,Eo> ComponentAction<A,Eo>.imap(crossinline f: (X) -> A): ComponentAction<X, Eo> {
    val orig = this
    return object: ComponentAction<X,Eo> {
        override fun emit(event: Eo) {
            orig.emit(event)
        }

        override fun setValue(value: X) {
            orig.setValue(f(value))
        }
    }
}

interface HComponent<in Ei, Eo, A, out B> {
    @Composable fun ComponentAction<A, Eo>.contents()
    fun ComponentAction<A,Eo>.onEvent(event: Ei)
    val events: Flow<Eo>
    val result: StateFlow<B>
}

@Composable
fun <Ei,Eo,A,B> HComponent<Ei, Eo, A, B>.contents(action: ComponentAction<A,Eo>) {
    action.contents()
}

fun <Ei,Eo,A,B> HComponent<Ei, Eo, A, B>.onEvent(event: Ei, action: ComponentAction<A,Eo>) {
    action.onEvent(event)
}

typealias HComponentDescription<C,Ei,Eo,A,B> =
    Description<C,A,HComponent<Ei,Eo,A,B>>

inline fun <C,Ei,Eo,A,B,X> HComponentDescription<C,Ei,Eo,A,B>.imap(
    crossinline f: (X) -> A,
    crossinline fInv: (A) -> X
): HComponentDescription<C,Ei,Eo,X,B> {
    val origDescr = this
    return object: HComponentDescription<C,Ei,Eo,X,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: X): HComponent<Ei, Eo, X, B> {
            val orig = origDescr.initialize(ctx, f(initialValue))
            return object: HComponent<Ei,Eo,X,B> {
                @Composable
                override fun ComponentAction<X, Eo>.contents() {
                    orig.contents(this.imap(fInv))
                }
                override fun ComponentAction<X, Eo>.onEvent(event: Ei) {
                    orig.onEvent(event, this.imap(fInv))
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}

fun <C,Ei,Eo,A,B,X> HComponentDescription<C,Ei,Eo,A,B>.omap(f: (B) -> X): HComponentDescription<C,Ei,Eo,A,X> {
    val origDescr = this
    return object: HComponentDescription<C,Ei,Eo,A,X> {

        override fun initialize(ctx: C, initialValue: A): HComponent<Ei, Eo, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: HComponent<Ei,Eo,A,X> {
                @Composable
                override fun ComponentAction<A, Eo>.contents() {
                    orig.contents(this)
                }
                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

                override fun ComponentAction<A, Eo>.onEvent(event: Ei) {
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
fun <Ei,Eo,A,B,C: IodineContext> HComponent<Ei,Eo,A,B>.getContents(ctx: C) {
    val component = this
    val newEvents = MutableSharedFlow<Eo>()
    component.events
        .onEach { event ->
            newEvents.emit(
                event
            )
        }
    val newComponent = object: HComponent<Ei,Eo,A,B> {
        @Composable
        override fun ComponentAction<A, Eo>.contents() {
            with(component) { contents() }
        }

        override fun ComponentAction<A, Eo>.onEvent(event: Ei) {
            with(component) { onEvent(event) }
        }

        override val events: Flow<Eo>
            get() = newEvents
        override val result: StateFlow<B>
            get() = component.result

    }
    val actions = object: ComponentAction<A,Eo> {
        override fun emit(event: Eo) {
            ctx.defaultScope.launch {
                newEvents.emit(event)
            }
        }

        override fun setValue(value: A) {
            // TODO: I think Component has to extend settable for this
            // to work.
            // component.onSetValue()
        }
    }
    with(newComponent) {
        actions.contents()
    }
}

inline fun <C,Ei,Eo,A,B> HComponentDescription<C,Ei,Eo,A,B>.wrap(crossinline f: @Composable () (@Composable () () -> Unit) -> Unit): HComponentDescription<C,Ei,Eo,A,B> {
    val origDescr = this
    return object: HComponentDescription<C,Ei,Eo,A,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): HComponent<Ei, Eo, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: HComponent<Ei,Eo,A,B> {
                @Composable
                override fun ComponentAction<A, Eo>.contents() {
                    f { with(orig) { contents() } }
                }

                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result

                override fun ComponentAction<A, Eo>.onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}

inline fun <C,D,Ei,Eo,A,B> HComponentDescription<C,Ei,Eo,A,B>.mapCtx(crossinline  f: (D) -> C): HComponentDescription<D,Ei,Eo,A,B> {
    val origDescr = this
    return object: HComponentDescription<D,Ei,Eo,A,B> {
        @Composable
        override fun initCompose(ctx: D) {
            origDescr.initCompose(f(ctx))
        }

        override fun initialize(ctx: D, initialValue: A): HComponent<Ei,Eo, A, B> {
            val orig = origDescr.initialize(f(ctx), initialValue)
            return object: HComponent<Ei,Eo,A,B> {
                @Composable
                override fun ComponentAction<A, Eo>.contents() {
                    with(orig) { contents() }
                }

                override val events: Flow<Eo>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
                override fun ComponentAction<A, Eo>.onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}

inline fun <C,Ei,Eo,X,A,B> HComponentDescription<C,Ei,Eo,A,B>.mapEvents(crossinline f: suspend (Eo) -> X): HComponentDescription<C,Ei,X,A,B> {
    val origDescr = this
    return object: HComponentDescription<C,Ei,X,A,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): HComponent<Ei, X, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: HComponent<Ei, X,A,B> {
                @Composable
                override fun ComponentAction<A, X>.contents() {
                    with(orig) { contents() }
                }

                override val events: Flow<X>
                    get() = orig.events.map(f)
                override val result: StateFlow<B>
                    get() = orig.result

                override fun ComponentAction<A, X>.onEvent(event: Ei) {
                    with(orig) { onEvent(event) }
                }
            }
        }
    }
}