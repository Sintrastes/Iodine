package com.bedelln.iodine.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import arrow.core.Either
import com.bedelln.iodine.forms.ValidatedForm
import com.bedelln.iodine.forms.ValidatingForm
import com.bedelln.iodine.forms.ValidatingFormDescription
import com.bedelln.iodine.forms.ValidationEvent
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

object InvalidInteger

/** A component for inputting integers. */
class IntEntry<C : IodineContext> : FormDescription<C, Any, ValidationEvent<Void>, String, Int?> by (
    ValidatedForm(
        object: ValidatingFormDescription<C, Unit, Void, String, Int, InvalidInteger> {

            override fun initialize(ctx: C, initialValue: String) =
                object: ValidatingForm<C, Unit, Void, String, Int, InvalidInteger>(initialValue) {

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

                    override val impl: Unit
                        get() = Unit
                    override val events: Flow<Void>
                        get() = emptyFlow()
                }
        }
    )
)
