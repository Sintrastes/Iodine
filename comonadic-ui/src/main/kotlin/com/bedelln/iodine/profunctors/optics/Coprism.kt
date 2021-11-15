
package com.bedelln.iodine.profunctors.optics

import arrow.core.Either
import com.bedelln.iodine.profunctors.classes.Cochoice
import com.bedelln.iodine.util.Hk2

interface Coprism<S,T,A,B> {
    fun <P> Cochoice<P>.transform(x: Hk2<P,A,B>): Hk2<P,S,T>
}

/** Helper function to build a Coprism. */
fun <S,T,A,B> coprism(to: (S) -> A, from: (B) -> Either<A, T>): Coprism<S,T,A,B> {
    TODO()
}