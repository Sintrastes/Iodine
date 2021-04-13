package com.bedelln.android.tools

import androidx.compose.runtime.Composable
import com.bedelln.iodine.Tool
import com.bedelln.iodine.ToolDescription
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