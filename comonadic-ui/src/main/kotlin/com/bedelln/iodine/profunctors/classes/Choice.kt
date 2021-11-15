package com.bedelln.iodine.profunctors.classes

import arrow.core.Either
import com.bedelln.iodine.util.Hk2

interface Choice<P> {
    fun <A,B,C> Hk2<P,A,B>.left(): Hk2<P, Either<A, C>, Either<B,C>>
    fun <A,B,C> Hk2<P,A,B>.right(): Hk2<P, Either<C,A>, Either<C,B>>
}