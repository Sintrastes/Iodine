package com.bedelln.iodine.util

import io.kindedj.Hk

/**
 * Interface witnessing the relationship between a comonad [W]
 *  and a monad in the form of an interface [I].
 */
interface Pairing<W,I> {
    /** Helper function to get an implementation of a monad
     * which manipulates the given state space. */
    fun <S> Hk<W, S>.getImpl(): I
}