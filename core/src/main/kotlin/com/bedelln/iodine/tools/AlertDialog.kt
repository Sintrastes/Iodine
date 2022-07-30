package com.bedelln.iodine.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
fun <A> CoroutineScope.FormDialog(
    title: String,
    form: Form<A, A>,
    initialValue: A
): Tool<A> {
    return object : Tool<A> {
        val currentValue = MutableStateFlow<A?>(null)

        val toolState = mutableStateOf<ToolState<A>>(
            ToolState.Initial
        )

        @Composable
        override fun UI(): ToolState<A> {
            var toolResult by toolState

            AlertDialog(
                onDismissRequest = {
                    toolResult = ToolState.Cancelled
                },
                title = {
                    Text(text = title)
                },
                text = {
                    val result = form(initialValue)
                    launch {
                        currentValue.emit(result)
                    }
                },
                buttons = {
                    Row(
                        modifier = Modifier.padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.padding(all = 8.dp),
                            onClick = {
                                toolResult = ToolState.Cancelled
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            modifier = Modifier.padding(all = 8.dp),
                            onClick = {
                                toolResult = if (currentValue.value == null) {
                                    ToolState.Initial
                                } else {
                                    ToolState.Returned(
                                        currentValue.value!!
                                    )
                                }
                            }
                        ) {
                            Text("Ok")
                        }
                    }
                }
            )

            return toolResult
        }

        override fun start() {

        }
    }
}