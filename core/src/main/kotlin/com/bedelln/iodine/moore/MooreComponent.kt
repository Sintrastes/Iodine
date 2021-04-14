package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.*
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.Component
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*

/** Abstract class for a component defined via the Elm Architecture. */
abstract class MooreComponent<C,E,Ei,S,A,B>(val initialState: S, val inputEvents: Flow<Ei>): ComponentDescription<C,E,A,B> {

    lateinit var state: State<S>

    abstract fun reducer(event: Ei, state: S): S

    val stateStream = MutableStateFlow(initialState).apply {
        inputEvents.onEach { event ->
            emit(reducer(event, value))
        }
    }

    abstract fun result(state: S): B

    @Composable
    abstract fun render(ctx: C, state: S)

    @Composable
    override fun initCompose(ctx: C) {
        state = stateStream.collectAsState()
    }

    override fun initialize(ctx: C, initialValue: A): Component<E, A, B> {
        return object: Component<E,A,B> {
            @Composable
            override fun contents() {
                render(ctx, state.value)
            }

            override val events: Flow<E>
                get() = emptyFlow()
            override val result: StateFlow<B>
                get() = stateStream
                    .mapStateFlow(::result)
        }
    }
}