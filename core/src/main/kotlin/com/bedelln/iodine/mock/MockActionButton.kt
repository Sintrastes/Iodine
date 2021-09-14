package com.bedelln.iodine.mock

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Description
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.ToolDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class MockActionButton<C: IodineContext>(
    val text: String,
    val action: ToolDescription<C, Unit, Unit>
): Description<C, Unit, MockActionButton.Impl> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Unit): Impl {
        return Impl()
    }

    object Click

    class Impl: Component<Click, Void, Unit, Unit> {
        override fun onEvent(event: Click) {
            TODO("Not yet implemented")
        }

        override val events: Flow<Void>
            get() = emptyFlow()
        override val result: StateFlow<Unit>
            get() = TODO("Not yet implemented")

        @Composable
        override fun contents() {
            TODO("Not yet implemented")
        }
    }
}

/** Simulate a clock action on a mock action button. */
fun MockActionButton.Impl.click() {

}