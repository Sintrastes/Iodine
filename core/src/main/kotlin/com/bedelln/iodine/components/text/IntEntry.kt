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

/** A component for inputting integers. */
class IntEntry : FormDescription<IodineContext, Any, ValidationEvent<Void>, String, Int?> by (
    ValidatedForm(
        object: ValidatingFormDescription<IodineContext, Unit, Void, String, Int, ValidationError> {

            override fun initialize(ctx: IodineContext, initialValue: String) =
                object: ValidatingForm<IodineContext, Unit, Void, String, Int, ValidationError>(initialValue) {

                    override fun view(input: String) =
                        input.toIntOrNull()?.let { Either.Right(it) }
                            ?: Either.Left(ValidationError.InvalidInteger)

                    @Composable
                    override fun contents(error: ValidationError?, contents: String) {
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
                            when (error) {
                                ValidationError.InvalidInteger -> {
                                    Text(
                                        text = "Not a valid integer",
                                        color = Color.Red,
                                        fontSize = 16.sp
                                    )
                                }
                                ValidationError.Empty -> {
                                    Text(
                                        text = "Please enter a valid integer",
                                        color = Color.Red,
                                        fontSize = 16.sp
                                    )
                                }
                                null -> { }
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
) {
    sealed interface ValidationError {
        object InvalidInteger : ValidationError
        object Empty: ValidationError
    }
}
