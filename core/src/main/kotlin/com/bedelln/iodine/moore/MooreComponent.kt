package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.*
import com.bedelln.iodine.*
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*

/** Abstract class for a component defined via the Elm Architecture. */
abstract class MooreComponent<C,Ei,Eo,S,A,B>(val initialState: S): HComponentDescription<C,Ei,Eo,A,B> {

    lateinit var state: State<S>

    abstract fun reducer(event: Ei, state: S): S

    val stateStream = MutableStateFlow(initialState).apply {
        // TODO: Fix this.
        // inputEvents.onEach { event ->
        //     emit(reducer(event, value))
        // }
    }

    abstract fun result(state: S): B

    @Composable
    abstract fun render(ctx: C, state: S)

    @Composable
    override fun initCompose(ctx: C) {
        state = stateStream.collectAsState()
    }

    override fun initialize(ctx: C, initialValue: A): HComponent<Ei, Eo, A, B> {
        return object: HComponent<Ei,Eo,A,B> {
            @Composable
            override fun ComponentAction<A, Eo>.contents() {
                render(ctx, state.value)
            }

            override val events: Flow<Eo>
                get() = emptyFlow()
            override val result: StateFlow<B>
                get() = stateStream
                    .mapStateFlow(::result)

            override fun ComponentAction<A, Eo>.onEvent(event: Ei) {
                TODO("Not yet implemented")
            }
        }
    }
}