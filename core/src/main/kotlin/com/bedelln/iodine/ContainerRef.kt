package com.bedelln.iodine

import androidx.compose.runtime.Composable

/** Interface for a "container", to which composable functions may be added to the view,
 * such as an OS window, an Android Activity, or an Android Fragment. */
interface ContainerRef {
    fun addToContents(f: @Composable() () -> Unit)
}