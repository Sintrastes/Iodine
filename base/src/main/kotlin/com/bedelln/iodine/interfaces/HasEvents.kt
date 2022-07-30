package com.bedelln.iodine.interfaces

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface HasEvents<out E> {
    val events: Flow<E> get() = flowOf()
}