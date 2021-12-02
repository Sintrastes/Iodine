package com.bedelln.iodine.interfaces

import kotlinx.coroutines.flow.StateFlow

interface HasState<out S> {
    val state: StateFlow<S>
}