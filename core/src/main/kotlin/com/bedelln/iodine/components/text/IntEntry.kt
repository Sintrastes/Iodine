package com.bedelln.iodine.components.text

import androidx.compose.runtime.Composable
import com.bedelln.iodine.Component
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.IodineContext

class IntEntry<C: IodineContext>(): ComponentDescription<C, Void, Int, Int> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: Int): Component<Void, Int, Int> {
        TODO("Not yet implemented")
    }
}