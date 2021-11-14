package com.bedelln.iodine.comonads

import io.kindedj.Hk

/**
 * The Day convolution of two covariant functors.
 *
 * Used in the context of comonadic components to construct a state space
 *  where one can preform at any time that actions of `F` as well as the actions
 *  of `G`.
 */
interface Day<F,G,A>: Hk<Day.W<F, G>, A> {
    class W<F,G>

    fun <B,C> runDay(x: Hk<F,B>, y: Hk<G,C>, f: (B,C) -> A): A
}