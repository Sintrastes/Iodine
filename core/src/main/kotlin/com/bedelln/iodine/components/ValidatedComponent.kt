package com.bedelln.iodine.components

import androidx.compose.runtime.Composable
import arrow.core.Either
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.SettableHComponentDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ValidationEvent<in E> {
    class SubmitForValidation<E>(): ValidationEvent<E>()
    data class Other<E>(val event: E): ValidationEvent<E>()
}

/** Helper class for building components with validation. */
class ValidatedComponent<C: IodineContext, Ei, Eo, A, B, Err>(
    val contents: SettableHComponentDescription<C, Ei, Eo, Pair<Err?, A>, Either<Pair<Err, A>, B>>
): HComponentDescription<C, ValidationEvent<Ei>, Eo, A, B?> {
    @Composable
    override fun initCompose(ctx: C) {
        contents.initCompose(ctx)
    }

    override fun initialize(ctx: C, initialValue: A): HComponent<ValidationEvent<Ei>, Eo, A, B?> {
        return object: HComponent<ValidationEvent<Ei>, Eo, A, B?> {

            val component = contents.initialize(ctx, Pair(null, initialValue))
            val resultFlow = MutableStateFlow(
                component.result.value.orNull()
            )

            @Composable
            override fun contents() {
                component.contents()
            }

            override fun onEvent(event: ValidationEvent<Ei>) = with(component) {
                if(event is ValidationEvent.SubmitForValidation) {
                    when(val result = result.value) {
                        is Either.Left -> {
                            ctx.setValue(result.value)
                            ctx.defaultScope.launch {
                                resultFlow.emit(
                                        component.result.value.orNull()
                                )
                            }
                        }
                    }
                }
            }

            override val events: Flow<Eo>
                get() = component.events
            override val result: StateFlow<B?>
                get() = resultFlow
        }
    }
}