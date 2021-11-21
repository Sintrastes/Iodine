package com.bedelln.iodine.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import com.bedelln.iodine.interfaces.Tool
import com.bedelln.iodine.interfaces.ToolDescription
import com.bedelln.iodine.desktop.ctx.SystemCtx
import com.bedelln.iodine.desktop.ctx.notify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** A tool for sending a notification to the system tray. */
class Notification<C: SystemCtx>(
    val title: String,
    val message: String,
    val type: Notification.Type = Notification.Type.Info
): ToolDescription<C, Void, Unit, Unit> {
    lateinit var notification: Notification

    @Composable
    override fun initCompose(ctx: C) {
        notification = rememberNotification(
            title,
            message,
            type
        )
    }

    override fun initialize(ctx: C, initialValue: Unit): Tool<C, Void, Unit> {
        return object: Tool<C, Void, Unit> {
            override suspend fun runTool(ctx: C) {
                ctx.trayState.sendNotification(
                    notification
                )
            }

            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}