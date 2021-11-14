package com.bedelln.iodine.comonads

import com.bedelln.iodine.util.Functor
import io.kindedj.Hk

interface Comonad<W> : Functor<W> {
    fun <A> Hk<W, A>.extract(): A
    fun <A> Hk<W, A>.duplicate(): Hk<W, Hk<W, A>>
}

data class Moore<E, A>(
    val value: A,
    val next: (E) -> Moore<E, A>
) : Hk<Moore.W<E>, A> {
    class W<E>()

    companion object {
        fun <E> comonad(): Comonad<Moore.W<E>> {
            TODO()
        }
    }
}

data class Store<S, A>(
    val state: S,
    val view: (S) -> A
) : Hk<Store.W<S>, A> {
    class W<S>()

    companion object {
        fun <S> comonad(): Comonad<Store.W<S>> {
            TODO()
        }
    }
}

data class Cofree<F, A>(
    val state: A,
    val next: Hk<F, Cofree<F,A>>
): Hk<Cofree.W<F>, A> {
    class W<F>
    companion object {
        fun <S> comonad(): Comonad<Store.W<S>> {
            TODO()
        }
    }
}