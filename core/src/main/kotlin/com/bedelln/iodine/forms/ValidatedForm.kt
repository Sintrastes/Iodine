package com.bedelln.iodine.forms

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class ValidationEvent<in E> {
    class SubmitForValidation<E>(): ValidationEvent<E>()
    data class Other<E>(val event: E): ValidationEvent<E>()
}

/** Helper class for building components with validation. */
class ValidatedForm<C: IodineContext, I, E, A, B, Err>(
    val contents: ValidatingFormDescription<C, I, E, A, B, Err>
): FormDescription<C, I, ValidationEvent<E>, A, B?> {
    @Composable
    override fun initCompose(ctx: C) {
        contents.initCompose(ctx)
    }

    override fun initialize(ctx: C, initialValue: A): Form<I,ValidationEvent<E>, A, B?> {
        return object: Form<I, ValidationEvent<E>, A, B?> {
            override val state: StateFlow<A>
                get() = component.state

            val component = contents
                .initialize(ctx, initialValue)

            val resultFlow = MutableStateFlow(
                component.result.value.orNull()
            )

            @Composable
            override fun contents(state: A) {
                component.contents(state)
            }

            override val events: Flow<ValidationEvent<E>>
                get() = component.events
                    .map { ValidationEvent.Other(it) }
            override val result: StateFlow<B?>
                get() = resultFlow

            override val impl: I
                get() = component.impl
        }
    }
}