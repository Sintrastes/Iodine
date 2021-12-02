package com.bedelln.iodine.android.tools

import androidx.compose.foundation.border
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bedelln.iodine.android.AndroidCtx
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AndroidAlertDialog<C, I, E, A, B>(
    val title: String,
    val contents: FormDescription<C, I, E, A, B>
): ToolDescription<C, E, A, B>
        where C : AndroidCtx,
              C: HasRef {

    lateinit var onFinish: MutableSharedFlow<B>

    val showDialogFlow = MutableStateFlow(false)
    lateinit var showState: State<Boolean>
    suspend fun showDialogAction() {
        showDialogFlow.emit(true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    override fun initCompose(ctx: C) {
        showState = showDialogFlow.collectAsState()
    }

    lateinit var _contents: Form<I, E, A, B>

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize(ctx: C, initialValue: A) = object : Tool<C, E, B> {

        init {
            onFinish = MutableSharedFlow()

            ctx.ref.addToContents {
                val showDialog by remember { showState }
                _contents = contents.initialize(ctx, initialValue)
                if (showDialog) {
                    AlertDialog(
                        title = { Text(title) },
                        onDismissRequest = {
                        },
                        modifier = Modifier.border(
                            width = 1.dp,
                            MaterialTheme.colors.primary
                        ),
                        text = {
                            // TODO: Fix this.
                            _contents.contents()
                        },
                        buttons = {
                            Button(
                                onClick = {
                                    ctx.defaultScope.launch {
                                        showDialogFlow.emit(false)
                                        val value = _contents.result.value
                                        onFinish.emit(value)
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

        override suspend fun runTool(ctx: C): B {
            showDialogAction()
            return onFinish.first()
        }

        override val events: Flow<E>
            get() = _contents.events
    }
}