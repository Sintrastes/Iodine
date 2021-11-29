package com.bedelln.iodine.desktop.tools

import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow

/**
 * Tool for launching a popup window from another window.
 */
class PopupWindow<C, I, E, A, B>(
    val title: String,
    val contents: FormDescription<C, I, E, A, B>
): ToolDescription<C, E, A, B>
  where C: IodineContext,
        C: HasRef {
    override fun initialize(ctx: C, initialValue: A): Tool<C, E, B> {
        return object: Tool<C,E,B> {
            override val events: Flow<E>
                get() = TODO("Not yet implemented")

            override suspend fun runTool(ctx: C): B {
                TODO("Not yet implemented")
            }
        }
    }
}