package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

/** Component modeled off of the Store comonad. */
abstract class StoreComponent<A,B>: HComponent<Void,Void,A,B> {
    abstract val input: StateFlow<A>
    abstract fun view(input: A): B
    override val result: StateFlow<B>
        get() = input.mapStateFlow(::view)

    override fun onEvent(event: Void) { }

    override val events: Flow<Void>
        get() = emptyFlow()
}

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