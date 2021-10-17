package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.StateFlow

abstract class MooreFormImpl<C: IodineContext,Ei,Eo,S,A,B>(
    initialState: S
): MooreComponentImpl<C, Ei, Eo, S, A>(initialState)

/**
 * Monadic extend function for a [MooreForm].
 */
inline fun <C: IodineContext, Ei, Eo, S, A, X, B> MooreComponentImpl<C,Ei,Eo,S,A>.extendM(
    crossinline f: (MooreComponentImpl<C,Ei,Eo,S,A>) -> StateFlow<B>
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