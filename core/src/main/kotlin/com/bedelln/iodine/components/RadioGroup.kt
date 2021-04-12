package com.bedelln.iodine.components

import androidx.compose.runtime.Composable
import com.bedelln.iodine.*

class RadioGroup<A, C: IodineContext>(val values: List<A>): ComponentDescription<C, Void, A, A> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: A): Component<Void, A, A> {
        TODO("Not yet implemented")
    }
}