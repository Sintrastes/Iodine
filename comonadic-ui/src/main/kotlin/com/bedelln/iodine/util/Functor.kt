package com.bedelln.iodine.util

import io.kindedj.Hk

interface Functor<F> {
    fun <A, B> Hk<F, A>.fmap(f: (A) -> B): Hk<F, B>
}