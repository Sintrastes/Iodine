package com.bedelln.iodine.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import arrow.core.Either
import com.bedelln.iodine.components.ValidatedComponent
import com.bedelln.iodine.components.ValidationEvent
import com.bedelln.iodine.interfaces.*
import com.bedelln.iodine.store.ValidatingStoreComponent
import com.bedelln.iodine.store.ValidatingStoreComponentDescription
import kotlinx.coroutines.launch

object InvalidInteger

/** A component for inputting integers. */
class IntEntry<C : IodineContext> : ComponentDescription<C, ValidationEvent<Void>, Void, String, Int?> by (
    ValidatedComponent<C, String, Int, InvalidInteger>(
        object : ValidatingStoreComponentDescription<C, String, Int, InvalidInteger> {

            override fun initialize(ctx: C, initialValue: String) =
                object: ValidatingStoreComponent<String, Int, InvalidInteger>(initialValue) {

                    override fun view(input: String) =
                        input.toIntOrNull()?.let { Either.Right(it) }
                            ?: Either.Left(InvalidInteger)

                    @Composable
                    override fun contents(error: InvalidInteger?, contents: String) {
                        Column {
                            TextField(
                                value = contents,
                                onValueChange = { newValue ->
                                    ctx.defaultScope.launch {
                                        mutInput.emit(newValue)
                                    }
                                },
                                label = {
                                    Text("")
                                }
                            )
                            if (error == InvalidInteger) {
                                Text(
                                    text = "Not a valid integer",
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
        }
    )
)
