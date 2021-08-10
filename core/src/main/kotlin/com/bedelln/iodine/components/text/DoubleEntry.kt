package com.bedelln.iodine.components.text

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.IodineContext

class DoubleEntry<C: IodineContext>(): ComponentDescription<C, Void, Void, Double, Double> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: Double): Component<Void, Void, Double, Double> {
        TODO("Not yet implemented")
    }
}