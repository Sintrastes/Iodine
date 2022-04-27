package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import com.bedelln.iodine.comonads.NaturalTransformation
import com.bedelln.iodine.util.Functor
import com.bedelln.iodine.util.Pairing
import io.kindedj.Hk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ComonadicViewModel<W,S,E,A>: Settable<A> {
    val stateSpace: Hk<W, S>
    val events: Flow<E>
}

interface ComonadicComponent<W,S,E,A>: ComonadicViewModel<W,S,E,A> {
    @Composable
    fun contents(state: S)
}

typealias ComonadicComponentDescription<Ctx,W,S,E,A>
        = Description<Ctx, A, ComonadicComponent<W,S,E,A>>

/**
 * Transform the type of comonad used in a comonadic component by
 *  supplying a natural transformation.
 *
 * Useful for "hiding" details of a component's interaction type.
 */
fun <F,G,S,E,A> ComonadicComponent<F,S,E,A>.transform(
    transformation: NaturalTransformation<F, G>
): ComonadicComponent<G,S,E,A> {
    val origComponent = this
    return object: ComonadicComponent<G,S,E,A> {
        override val stateSpace: Hk<G, S>
            get() = transformation(origComponent.stateSpace)

        @Composable
        override fun contents(state: S) {
            return origComponent.contents(state)
        }

        override val events: Flow<E>
            get() = origComponent.events
    }
}

/**
 * Map the type of state used by a comonadic component.
 *
 * Useful for hiding or adapting the type of state exposed by a
 *  comonadic component.
 */
context(Functor<F>)
fun <F,S,X,E,A> ComonadicComponent<F,S,E,A>.mapState(
    to: (S) -> X,
    from: (X) -> S
): ComonadicComponent<F,X,E,A> {
    val origComponent = this
    return object: ComonadicComponent<F,X,E,A> {
        override val stateSpace: Hk<F, X>
            get() = origComponent.stateSpace.fmap(to)

        @Composable
        override fun contents(state: X) {
            return origComponent.contents(from(state))
        }

        override val events: Flow<E>
            get() = origComponent.events
    }
}

/**
 * Helper function converting a comonadic component into a standard Iodine component.
 *
 * This relationship cannot be witnessed by subtyping, as it depends on codegen
 *  in order to be able to construct the necessary Pairing relationship.
 */
context(Pairing<W, I>)
fun <W,S,E,A,I> ComonadicComponent<W,S,E,A>.asComponent(): Component<I,E,A> {
    val component = this
    return object: Component<I,E,A> {
        override val impl: I =
            stateSpace.getImpl()


        val state: StateFlow<S> get() = TODO()

        override val events: Flow<E>
            get() = component.events

        @Composable
        override fun contents() {
            val componentState = state.collectAsState()
            val st by remember { componentState }
            component.contents(st)
        }
    }
}