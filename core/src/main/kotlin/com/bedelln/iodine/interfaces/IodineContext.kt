package com.bedelln.iodine.interfaces

import kotlinx.coroutines.CoroutineScope

/** The minimal context for defining Iodine components. */
interface IodineContext {
    val defaultScope: CoroutineScope
}