package com.bedelln.iodine.android.tools

import android.widget.Toast
import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Tool
import com.bedelln.iodine.interfaces.ToolDescription
import com.bedelln.iodine.android.AndroidCtx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class Toast(val message: String): ToolDescription<AndroidCtx, Void, Unit, Unit> {
    override fun initialize(
        ctx: AndroidCtx,
        initialValue: Unit
    ): Tool<AndroidCtx, Void, Unit> {
        return object: Tool<AndroidCtx, Void, Unit> {
            override suspend fun runTool(ctx: AndroidCtx) {
                Toast.makeText(ctx.defaultCtx, message, Toast.LENGTH_SHORT)
                    .show()
            }

            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}