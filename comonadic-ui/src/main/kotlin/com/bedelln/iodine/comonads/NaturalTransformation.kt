package com.bedelln.iodine.comonads

import io.kindedj.Hk

interface NaturalTransformation<F,G> {
    operator fun <A> invoke(x: Hk<F,A>): Hk<G, A>
}