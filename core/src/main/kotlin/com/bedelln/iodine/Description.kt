package com.bedelln.iodine

import androidx.compose.runtime.Composable

interface Description<in C,in A,out B> {
    @Composable
    fun initCompose(ctx: C)

    fun initialize(ctx: C, initialValue: A): B
}