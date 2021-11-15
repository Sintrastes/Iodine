package com.bedelln.iodine.profunctors.classes

import com.bedelln.iodine.util.Hk2

interface Strong<P> {
    fun <A,B,C> Hk2<P,A,B>.first(): Hk2<P, Pair<A, C>, Pair<B, C>>
    fun <A,B,C> Hk2<P,A,B>.second(): Hk2<P, Pair<C, A>, Pair<C, B>>
}