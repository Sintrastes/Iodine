package com.bedelln.iodine.store

import com.bedelln.iodine.interfaces.Form
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Component modeled off of the Store comonad.
 *
 * Note that the sense in which a [StoreForm] is modeled off a Store comonad
 *  is different from the sense of a ComonadicComponent is based off of a comonad.
 */
abstract class StoreForm<I, E, A, B>: Form<I, E, A, B> {
    abstract fun view(input: A): B
    final override val result: StateFlow<B>
        get() = state.mapStateFlow(::view)
}