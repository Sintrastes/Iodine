package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable

interface Displayable<C> {
    @Composable
    fun C.display()
}