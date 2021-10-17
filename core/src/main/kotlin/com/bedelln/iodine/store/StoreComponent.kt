package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import arrow.core.Either
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*

/** Component modeled off of the Store comonad. */
abstract class StoreComponent<A,B>: Component<Void,Void,A,B> {
    abstract val input: StateFlow<A>
    abstract fun view(input: A): B
    final override val result: StateFlow<B>
        get() = input.mapStateFlow(::view)

    override fun onEvent(event: Void) { }

    override val events: Flow<Void>
        get() = emptyFlow()
}

/** A special type of store component to facilitate developing components with validation. */
abstract class ValidatingStoreComponent<A,B,Err>(val initialValue: A): StoreComponent<A, Either<Err, B>>() {
    val errorFlow
        get() = MutableStateFlow(result.value.swap().orNull())

    protected val mutInput
        = MutableStateFlow(initialValue)

    override val input
        = mutInput

    @Composable
    abstract fun contents(error: Err?, contents: A)

    @Composable
    override fun contents() {
        val errorState = errorFlow.collectAsState()
        val contentsState = mutInput.collectAsState()
        val error by remember { errorState }
        val contents by remember { contentsState }
        contents(error, contents)
    }
}

typealias ValidatingStoreComponentDescription<C,A,B,Err>
    = Description<C,A,ValidatingStoreComponent<A,B,Err>>

typealias StoreComponentDescription<C, A,B> = Description<C,A,StoreComponent<A,B>>

abstract class SettableStoreComponent<C: IodineContext, A,B>: StoreComponent<A, B>(), Settable<C,A>

typealias SettableStoreComponentDescription<C, A,B> = Description<C,A,SettableStoreComponent<C,A,B>>

fun <A,B> StoreComponent<A,B>.extractM(): StateFlow<B> {
    return this.result
}

inline fun <X,A,B> StoreComponent<X,A>.extendM(crossinline f: (StoreComponent<X,A>) -> StateFlow<B>): StoreComponent<X,B> {
    val component = this
    return object: StoreComponent<X, B>() {

        val resultFlow = f(component)

        @Composable
        override fun contents() {
            component.contents()
        }

        override fun view(input: X): B {
            return resultFlow.value
        }
        override val input: StateFlow<X>
            get() = component.input
    }
}