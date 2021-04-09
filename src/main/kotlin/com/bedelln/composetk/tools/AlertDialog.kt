package com.bedelln.composetk.tools

import androidx.compose.foundation.border
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DesktopDialogProperties
import com.bedelln.composetk.Tool
import com.bedelln.composetk.ToolDescription
import com.bedelln.composetk.desktop.WindowCtx

class AlertDialog(val title: String, val contents: @Composable() () -> Unit): ToolDescription<WindowCtx, Unit, Unit> {

    var showDialogAction: (() -> Unit)? = null

    override fun initialize(ctx: WindowCtx, initialValue: Unit) = object : Tool<WindowCtx, Unit> {
        init {
            println("Calling alert dialog initialize.")
            ctx.window.addToContents {
                var showDialog by remember { mutableStateOf(false) }
                showDialogAction = { showDialog = true }
                if (showDialog) {
                    AlertDialog(
                        title = { Text(title) },
                        onDismissRequest = {
                        },
                        properties = DesktopDialogProperties(undecorated = true),
                        modifier = Modifier.border(
                            width = 1.dp,
                            MaterialTheme.colors.primary
                        ),
                        text = contents,
                        buttons = {
                            Button(
                                onClick = {
                                    println("Alert dialog on click")
                                    showDialog = false
                                },
                                content = {
                                    Text("Ok")
                                }
                            )
                        }
                    )
                }
            }
        }

        override suspend fun runTool(ctx: WindowCtx) {
            println("Running alert dialog tool.")
            showDialogAction!!()
        }
    }
}