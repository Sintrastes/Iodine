package com.bedelln.iodine.comonads

import io.kindedj.Hk

/**
 * The product of two functors.
 */
data class Product<F,G,A>(
    val first: Hk<F,A>,
    val second: Hk<G, A>
): Hk<Product.W<F, G>, A> {
    class W<F,G>
}