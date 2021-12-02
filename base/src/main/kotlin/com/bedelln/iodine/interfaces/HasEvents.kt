package com.bedelln.iodine.interfaces

import kotlinx.coroutines.flow.Flow

interface HasEvents<out E> {
    val events: Flow<E>
}