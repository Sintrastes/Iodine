package com.bedelln.iodine

import kotlinx.coroutines.CoroutineScope

/** The minimal context for defining Iodine components. */
interface IodineContext {
    val defaultScope: CoroutineScope
}