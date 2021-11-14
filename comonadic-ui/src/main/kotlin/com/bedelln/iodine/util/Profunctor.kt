package com.bedelln.iodine.util

import io.kindedj.Hk

typealias Hk2<P,A,B> = Hk<Hk<P, A>, B>

interface Profunctor<P,A,B> {
    fun <P,A,B,X> Hk2<P,A,B>.imap(f: (X) -> A): Hk2<P,X,B>
    fun <P,A,B,X> Hk2<P,A,B>.omap(f: (B) -> X): Hk2<P,A,X>
}