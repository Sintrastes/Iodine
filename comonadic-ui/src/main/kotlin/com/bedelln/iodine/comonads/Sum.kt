package com.bedelln.iodine.comonads

import io.kindedj.Hk

/**
 * The sum of two functors
 */
sealed class Sum<F,G,A>: Hk<Sum.W<F,G>,A> {
    class W<F,G>
    data class InL<F,G,A>(val value: Hk<F,A>): Sum<F,G,A>()
    data class InR<F,G,A>(val value: Hk<G,A>): Sum<F,G,A>()
}