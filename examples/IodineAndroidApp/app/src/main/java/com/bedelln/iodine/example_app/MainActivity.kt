package com.bedelln.iodine.example_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.bedelln.iodine.Tool
import com.bedelln.iodine.ToolDescription
import com.bedelln.iodine.android.ActivityCtx
import com.bedelln.iodine.android.IodineActivity
import com.bedelln.iodine.components.*

class MainActivity: IodineActivity<ActivityCtx, Void, Unit, Unit>(Unit) {
    override val contextInitializer = { it: ActivityCtx -> it }

    override val contents = ActionButton(
        text = "Hello, Android!",
        action = object: ToolDescription<ActivityCtx,Unit,Unit> {
            @Composable
            override fun initCompose(ctx: ActivityCtx) { }

            override fun initialize(
                ctx: ActivityCtx,
                initialValue: Unit
            ): Tool<ActivityCtx, Unit> {
                return object: Tool<ActivityCtx, Unit> {
                    override suspend fun runTool(ctx: ActivityCtx) {
                        // noop
                    }
                }
            }
        }
    )
}