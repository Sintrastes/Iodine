package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable

interface Description<in C,in A,out B> {
    @Composable
    fun initCompose(ctx: C)

    fun initialize(ctx: C, initialValue: A): B
}