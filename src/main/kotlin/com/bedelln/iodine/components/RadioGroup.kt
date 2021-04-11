package com.bedelln.iodine.components

import androidx.compose.runtime.Composable
import com.bedelln.iodine.*
import com.bedelln.iodine.desktop.ctx.WindowCtx

class RadioGroup<A>(val values: List<A>): ComponentDescription<WindowCtx, Void, A, A> {
    @Composable
    override fun initCompose(ctx: WindowCtx) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: WindowCtx, initialValue: A): Component<Void, A, A> {
        TODO("Not yet implemented")
    }
}