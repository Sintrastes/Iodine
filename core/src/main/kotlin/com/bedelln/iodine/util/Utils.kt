package com.bedelln.iodine.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

inline fun <A,B> StateFlow<A>.mapStateFlow(crossinline f: (A) -> B): StateFlow<B> {
    val flow = MutableStateFlow(f(this.value))
    this.onEach {
        flow.emit(f(it))
    }
    return flow
}