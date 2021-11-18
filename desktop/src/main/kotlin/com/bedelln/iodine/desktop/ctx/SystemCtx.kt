package com.bedelln.iodine.desktop.ctx

import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.bedelln.iodine.interfaces.IodineContext

/** Minimal Iodine context for a Compose for desktop application. */
interface SystemCtx: IodineContext, ApplicationScope {
    val trayState: TrayState
}

/** Send a Desktop notification. */
fun SystemCtx.notify(
    title: String,
    message: String,
    type: Notification.Type = Notification.Type.None
) {
    trayState.sendNotification(
        Notification(
            title,
            message,
            type
        )
    )
}