package com.bedelln.iodine.components.text

import arrow.core.Either
import com.bedelln.iodine.interfaces.Form
import com.bedelln.iodine.interfaces.IodineContext

/*
class DoubleEntry: ValidatingTextEntry<Double, DoubleEntry.FormatError>() {
    override fun validate(input: String): Either<FormatError, Double> =
        if (input.isEmpty())
            Either.Left(FormatError.EmptyInput)
        else
            input.toDoubleOrNull()
                ?.let { Either.Right(it) }
                ?: Either.Left(FormatError.ParseError)

    override fun errorMessage(error: FormatError) = when(error) {
        FormatError.ParseError -> "Invalid double"
        FormatError.EmptyInput -> "Please enter a valid double."
    }

    sealed interface FormatError {
        object ParseError: FormatError
        object EmptyInput: FormatError
    }
}
 */