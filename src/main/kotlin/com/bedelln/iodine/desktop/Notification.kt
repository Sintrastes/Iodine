package com.bedelln.iodine.desktop

import androidx.compose.runtime.Composable
import com.bedelln.iodine.Tool
import com.bedelln.iodine.ToolDescription
import com.bedelln.iodine.desktop.ctx.SystemCtx
import com.bedelln.iodine.desktop.ctx.notify

class Notification<C: SystemCtx>(val title: String, val message: String): ToolDescription<C,Unit,Unit> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Unit): Tool<C, Unit> {
        return object: Tool<C,Unit> {
            override suspend fun runTool(ctx: C) {
                ctx.notify(title, message)
            }
        }
    }
}