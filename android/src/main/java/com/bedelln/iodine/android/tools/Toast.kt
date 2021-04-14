package com.bedelln.iodine.android.tools

import android.widget.Toast
import androidx.compose.runtime.Composable
import com.bedelln.iodine.Tool
import com.bedelln.iodine.ToolDescription
import com.bedelln.iodine.android.AndroidCtx

class Toast(val message: String): ToolDescription<AndroidCtx, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: AndroidCtx) {}

    override fun initialize(ctx: AndroidCtx, initialValue: Unit): Tool<AndroidCtx, Unit> {
        return object: Tool<AndroidCtx,Unit> {
            override suspend fun runTool(ctx: AndroidCtx) {
                Toast.makeText(ctx.defaultCtx, message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}