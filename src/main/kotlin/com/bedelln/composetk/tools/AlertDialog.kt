package com.bedelln.composetk.tools

import androidx.compose.foundation.border
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DesktopDialogProperties
import com.bedelln.composetk.ComponentDescription
import com.bedelln.composetk.Tool
import com.bedelln.composetk.ToolDescription
import com.bedelln.composetk.desktop.WindowCtx
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class AlertDialog<A,B>(val title: String, val contents: ComponentDescription<WindowCtx,Void, A,B>): ToolDescription<WindowCtx, A, B> {

    lateinit var onFinish: MutableSharedFlow<B>
    lateinit var showDialogAction: () -> Unit

    override fun initialize(ctx: WindowCtx, initialValue: A) = object : Tool<WindowCtx, B> {

        init {
            onFinish = MutableSharedFlow()

            ctx.window.addToContents {
                var showDialog by remember { mutableStateOf(false) }
                val _contents = contents.initialize(ctx, initialValue)
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
                        text = { _contents.contents() },
                        buttons = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    ctx.coroutineScope.launch {
                                        onFinish.emit(_contents.result.value)
                                    }
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

        override suspend fun runTool(ctx: WindowCtx): B {
            showDialogAction()
            return onFinish.single()
        }
    }
}