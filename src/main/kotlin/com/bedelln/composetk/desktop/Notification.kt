package com.bedelln.composetk.desktop

import androidx.compose.runtime.Composable
import com.bedelln.composetk.Tool
import com.bedelln.composetk.ToolDescription
import com.bedelln.composetk.desktop.ctx.SystemCtx
import com.bedelln.composetk.desktop.ctx.notify

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