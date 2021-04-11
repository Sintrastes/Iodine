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
import com.bedelln.composetk.desktop.ctx.WindowCtx
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

// Note: This should probably be SystemCtx -- an alert dialog could be launched from anywhere.
class AlertDialog<A,B>(val title: String, val contents: ComponentDescription<WindowCtx,Void, A,B>): ToolDescription<WindowCtx, A, B> {

    lateinit var onFinish: MutableSharedFlow<B>

    val showDialogFlow = MutableStateFlow(false)
    lateinit var showState: State<Boolean>
    suspend fun showDialogAction() {
        showDialogFlow.emit(true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    override fun initCompose(ctx: WindowCtx) {
        showState = showDialogFlow.collectAsState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize(ctx: WindowCtx, initialValue: A) = object : Tool<WindowCtx, B> {

        init {
            onFinish = MutableSharedFlow()

            ctx.window.addToContents {
                val showDialog by remember { showState }
                val _contents = contents.initialize(ctx, initialValue)
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
                                    ctx.windowScope.launch {
                                        showDialogFlow.emit(false)
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