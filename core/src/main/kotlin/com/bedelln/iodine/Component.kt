package com.bedelln.iodine

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.launch

interface Settable<in C, in A> {
    fun C.setValue(newValue: A)
}

interface HComponent<in Ei, out Eo, in A, out B> {
    @Composable fun contents()
    fun onEvent(event: Ei)
    val events: Flow<Eo>
    val result: StateFlow<B>
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

fun <C,Ei,Eo,A,B,X> HComponentDescription<C,Ei,Eo,A,B>.omap(f: (B) -> X): HComponentDescription<C,Ei,Eo,A,X> {
    val origDescr = this
    return object: HComponentDescription<C,Ei,Eo,A,X> {

        override fun initialize(ctx: C, initialValue: A): HComponent<Ei, Eo, A, X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: HComponent<Ei,Eo,A,X> {
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

inline fun <C,Ei,Eo,A,B> WrappedComponent(crossinline layout: @Composable () (@Composable () () -> Unit) -> Unit, component: HComponentDescription<C,Ei,Eo,A,B>): HComponentDescription<C,Ei,Eo,A,B>
    = component.wrap(layout)

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

inline fun <C,Ei,Eo,A,B> HComponentDescription<C,Ei,Eo,A,B>.ignoreEvents(
): HComponentDescription<C,Void,Void,A,B> {
    val origDescr = this
    return object: HComponentDescription<C,Void,Void,A,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): HComponent<Void, Void, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: HComponent<Void,Void,A,B> {
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