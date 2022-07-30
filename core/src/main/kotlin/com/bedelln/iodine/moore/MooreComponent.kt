package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Interface for a view model which acts by supplying an event handler
 * for input events. */
interface EventInterface<E> {
    fun onEvent(event: E)
}

/*
typealias MooreComponent<C,Ei,Eo,A>
    = MooreComponentImpl<C,Ei,Eo,*,A>
 */

/**
 * Abstract class for a component defined via the Elm Architecture.
 *
 * Can be constructed by supplying a [reducer], an initial state, and
 * a [render] function
 *
 * Isomorphic to ComonadicComponentImpl<Ctx, Moore<Ei>, Eo, S, A>.
 *
 * @param initialState The initial state of the component.
 */
/*
abstract class MooreComponentImpl<C: IodineContext,Ei,Eo,S,A>(
    val initialState: S
): ComponentDescriptionImpl<C,EventInterface<Ei>,Eo,S,A> {

    abstract fun reducer(event: Ei, state: S): S

    @Composable
    abstract fun C.render(state: S)

    val stateStream = MutableStateFlow(initialState)

    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: A): ComponentImpl<EventInterface<Ei>, Eo, S, A> {
        return object: ComponentImpl<EventInterface<Ei>, Eo, S, A> {
            @Composable
            override fun contents(state: S) {
                ctx.render(state)
            }

            override val events: Flow<Eo>
                get() = emptyFlow()

            override val impl: EventInterface<Ei> = object: EventInterface<Ei> {
                override fun onEvent(event: Ei) {
                    ctx.defaultScope.launch {
                        stateStream.emit(reducer(event, state.value))
                    }
                }
            }

            override val state: StateFlow<S>
                get() = stateStream
        }
    }
}

/**
 * Monadic extend function for a [MooreComponent].
 */
inline fun <C: IodineContext, Ei, Eo, S, A, X, B> MooreComponentImpl<C,Ei,Eo,S,A>.extendM(
    crossinline f: (MooreComponentImpl<C,Ei,Eo,S,A>) -> StateFlow<B>
): MooreComponentImpl<C,Ei,Eo,S,A> = run {
    val component = this
    object : MooreComponentImpl<C, Ei, Eo, S, A>(component.initialState) {
        override fun reducer(event: Ei, state: S): S =
            component.reducer(event, state)

        @Composable
        override fun C.render(state: S) {
            val ctx = this
            with(component) { ctx.render(state) }
        }
    }
}
 */