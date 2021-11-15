package com.bedelln.iodine.profunctors.classes

import arrow.core.Either
import com.bedelln.iodine.util.Hk2

interface Cochoice<P> {
    fun <A,B,C> Hk2<P, Either<A, C>,Either<B,C>>.unLeft(): Hk2<P,A,B>
    fun <A,B,C> Hk2<P,Either<C,A>,Either<C,B>>.unRight(): Hk2<P,A,B>
}