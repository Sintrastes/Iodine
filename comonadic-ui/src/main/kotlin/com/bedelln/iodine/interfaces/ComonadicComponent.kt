package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import com.bedelln.iodine.comonads.NaturalTransformation
import com.bedelln.iodine.util.Functor
import com.bedelln.iodine.util.Given
import io.kindedj.Hk

interface ComonadicViewModel<W,S> {
    val state: Hk<W, S>
}

interface ComonadicComponent<W,S>: ComonadicViewModel<W,S> {
    @Composable
    fun contents(state: S)
}

/**
 * Transform the type of comonad used in a comonadic component by
 *  supplying a natural transformation.
 *
 * Useful for "hiding" details of a component's interaction type.
 */
fun <F,G,S> ComonadicComponent<F,S>.transform(
    transformation: NaturalTransformation<F, G>
): ComonadicComponent<G,S> {
    val origComponent = this
    return object: ComonadicComponent<G,S> {
        override val state: Hk<G, S>
            get() = transformation(origComponent.state)

        @Composable
        override fun contents(state: S) {
            return origComponent.contents(state)
        }
    }
}

/**
 * Map the type of state used by a comonadic component.
 *
 * Useful for hiding or adapting the type of state exposed by a
 *  comonadic component.
 */
fun <F,S,X> ComonadicComponent<F,S>.mapState(
    @Given functor: Functor<F>,
    to: (S) -> X,
    from: (X) -> S
): ComonadicComponent<F,X> {
    val origComponent = this
    return object: ComonadicComponent<F,X> {
        override val state: Hk<F, X>
            get() = with(functor) { origComponent.state.fmap(to) }

        @Composable
        override fun contents(state: X) {
            return origComponent.contents(from(state))
        }
    }
}