package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Abstract class for a component defined via the Elm Architecture. */
abstract class MooreComponent<C: IodineContext,Ei,Eo,S,A,B>(val initialState: S): HComponentDescription<C, Ei, Eo, A, B> {

    lateinit var state: State<S>

    abstract fun reducer(event: Ei, state: S): S

    val stateStream = MutableStateFlow(initialState)

    abstract fun result(state: S): B

    @Composable
    abstract fun render(ctx: C, state: S)

    @Composable
    override fun initCompose(ctx: C) {
        state = stateStream.collectAsState()
    }

    override fun initialize(ctx: C, initialValue: A): HComponent<Ei, Eo, A, B> {
        return object: HComponent<Ei, Eo, A, B> {
            @Composable
            override fun contents() {
                render(ctx, state.value)
            }

            override val events: Flow<Eo>
                get() = emptyFlow()
            override val result: StateFlow<B>
                get() = stateStream
                    .mapStateFlow(::result)

            override fun onEvent(event: Ei) {
                ctx.defaultScope.launch {
                    stateStream.emit(reducer(event, state.value))
                }
            }
        }
    }
}

inline fun <C: IodineContext, Ei, Eo, S, A, X, B> MooreComponent<C,Ei,Eo,S,A,X>.extendM(
        crossinline f: (MooreComponent<C,Ei,Eo,S,A,X>) -> StateFlow<B>
): MooreComponent<C,Ei,Eo,S,A,B> = run {
    val component = this
    object : MooreComponent<C, Ei, Eo, S, A, B>(this.initialState) {
        override fun reducer(event: Ei, state: S): S =
            component.reducer(event, state)

        override fun result(state: S): B =
            f(component).value

        @Composable
        override fun render(ctx: C, state: S) {
            component.render(ctx, state)
        }
    }
}