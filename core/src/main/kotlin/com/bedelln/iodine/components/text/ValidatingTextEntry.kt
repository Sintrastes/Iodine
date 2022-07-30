package com.bedelln.iodine.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import arrow.core.Either
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * An abstract class for a text entry that preforms some validation, and
 *  displays a red error message on errors encountered.
 */
/*
abstract class ValidatingTextEntry<A,E>
    : ValidatingFormDescription<IodineContext, Unit, Void, String, A, E> {
        abstract fun validate(input: String): Either<E, A>
        abstract fun errorMessage(error: E): String

    override fun initialize(ctx: IodineContext, initialValue: String) =
        object: ValidatingForm<IodineContext, Unit, Void, String, A, E>(initialValue) {

            override fun view(input: String) = validate(input)

            @Composable
            override fun contents(error: E?, contents: String) {
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
                    if (error != null) {
                        Text(
                            text = errorMessage(error),
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
}
 */