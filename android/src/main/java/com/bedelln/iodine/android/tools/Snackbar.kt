package com.bedelln.iodine.android.tools

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Tool
import com.bedelln.iodine.interfaces.ToolDescription
import com.bedelln.iodine.android.AndroidCtx

class Snackbar: ToolDescription<AndroidCtx, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: AndroidCtx) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: AndroidCtx, initialValue: Unit): Tool<AndroidCtx, Unit> {
        TODO("Not yet implemented")
    }
}