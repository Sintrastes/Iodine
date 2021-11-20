package com.bedelln.iodine.android.tools

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Tool
import com.bedelln.iodine.interfaces.ToolDescription
import com.bedelln.iodine.android.AndroidCtx

class Snackbar: ToolDescription<AndroidCtx, Void, Unit, Unit> {
    override fun initialize(
        ctx: AndroidCtx,
        initialValue: Unit
    ): Tool<AndroidCtx, Void, Unit> {
        TODO("Not yet implemented")
    }
}