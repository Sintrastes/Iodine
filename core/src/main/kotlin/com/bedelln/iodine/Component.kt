package com.bedelln.iodine

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.*

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

fun <C,E,A,B,X> ComponentDescription<C,E,A,B>.omap(f: (B) -> X): ComponentDescription<C,E,A,X> {
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
                    get() = run {
                        val flow = MutableStateFlow(f(orig.result.value))
                        val mappedFlow = orig.result.map { f(it) }
                        mappedFlow.onEach {
                            flow.emit(it)
                        }
                        flow
                    }
            }
        }

        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }
    }
}

inline fun <C,E,A,B> ComponentDescription<C,E,A,B>.wrap(crossinline f: @Composable () (@Composable () () -> Unit) -> Unit): ComponentDescription<C,E,A,B> {
    val origDescr = this
    return object: ComponentDescription<C,E,A,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<E, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<E,A,B> {
                @Composable
                override fun contents() {
                    f { orig.contents() }
                }

                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}

inline fun <C,D,E,A,B> ComponentDescription<C,E,A,B>.mapCtx(crossinline  f: (D) -> C): ComponentDescription<D,E,A,B> {
    val origDescr = this
    return object: ComponentDescription<D,E,A,B> {
        @Composable
        override fun initCompose(ctx: D) {
            origDescr.initCompose(f(ctx))
        }

        override fun initialize(ctx: D, initialValue: A): Component<E, A, B> {
            val orig = origDescr.initialize(f(ctx), initialValue)
            return object: Component<E,A,B> {
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

inline fun <C,E,X,A,B> ComponentDescription<C,E,A,B>.mapEvents(crossinline f: suspend (E) -> X): ComponentDescription<C,X,A,B> {
    val origDescr = this
    return object: ComponentDescription<C,X,A,B> {
        @Composable
        override fun initCompose(ctx: C) {
            origDescr.initCompose(ctx)
        }

        override fun initialize(ctx: C, initialValue: A): Component<X, A, B> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: Component<X,A,B> {
                @Composable
                override fun contents() {
                    orig.contents()
                }

                override val events: Flow<X>
                    get() = orig.events.map(f)
                override val result: StateFlow<B>
                    get() = orig.result
            }
        }
    }
}