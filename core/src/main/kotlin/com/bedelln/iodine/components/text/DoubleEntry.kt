package com.bedelln.iodine.components.text

import com.bedelln.iodine.Component
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.IodineContext

class DoubleEntry<C: IodineContext>(): ComponentDescription<C, Void, Double, Double> {
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: Double): Component<Void, Double, Double> {
        TODO("Not yet implemented")
    }
}