package com.bedelln.iodine.components

import androidx.compose.runtime.Composable
import arrow.core.Either
import com.bedelln.iodine.Component
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.IodineContext

sealed class ValidationEvent<in E> {
    class SubmitForValidation<E>(): ValidationEvent<E>()
    data class Other<E>(val event: E): ValidationEvent<E>()
}

class ValidatedComponent<C: IodineContext, Ei, Eo, A, B, Err>(
    contents: ComponentDescription<C, Ei, Eo, Pair<Err?,A>, Either<B, Err>>
): ComponentDescription<C, ValidationEvent<Ei>, Eo, A, B> {
    @Composable
    override fun initCompose(ctx: C) {
        TODO("Not yet implemented")
    }

    override fun initialize(ctx: C, initialValue: A): Component<ValidationEvent<Ei>, Eo, A, B> {
        TODO("Not yet implemented")
    }
}