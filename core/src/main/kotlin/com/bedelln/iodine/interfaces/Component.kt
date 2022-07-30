package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/*

typealias Component<A, B, Act> = ComponentImpl<A, B, Act, *>
 */

interface Component<out I, out E, in A>: HasImpl<I>, HasEvents<E> {
    @Composable
    fun contents(initialValue: A)

    @Composable
    operator fun invoke(p1: A) {
        contents(p1)
    }
}

// TODO: Need imap, mapEvents

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