package com.bedelln.composetk

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

fun interface Description<in C,in A,out B> {
    fun initialize(ctx: C, initialValue: A): B
}

interface Settable<in C, in A> {
    fun C.setValue(newValue: A)
}

interface Component<out E,in A,out B> {
    @Composable fun contents(input: A)
    val events: Flow<E>
    val result: StateFlow<B>
}

typealias ComponentDescription<C,E,A,B> =
    Description<C,A,Component<E,A,B>>