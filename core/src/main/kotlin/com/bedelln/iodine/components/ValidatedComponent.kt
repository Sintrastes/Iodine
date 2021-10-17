package com.bedelln.iodine.components

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.store.ValidatingStoreComponentDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ValidationEvent<in E> {
    class SubmitForValidation<E>(): ValidationEvent<E>()
    data class Other<E>(val event: E): ValidationEvent<E>()
}

/** Helper class for building components with validation. */
class ValidatedComponent<C: IodineContext, A, B, Err>(
    val contents: ValidatingStoreComponentDescription<C, A, B, Err>
): ComponentDescription<C, ValidationEvent<Void>, Void, A, B?> {
    @Composable
    override fun initCompose(ctx: C) {
        contents.initCompose(ctx)
    }

    override fun initialize(ctx: C, initialValue: A): Component<ValidationEvent<Void>, Void, A, B?> {
        return object: Component<ValidationEvent<Void>, Void, A, B?> {

            val component = contents
                .initialize(ctx, initialValue)

            val resultFlow = MutableStateFlow(
                component.result.value.orNull()
            )

            @Composable
            override fun contents() {
                component.contents()
            }

            override fun onEvent(event: ValidationEvent<Void>) = with(component) {
                if(event is ValidationEvent.SubmitForValidation) {
                    ctx.defaultScope.launch {
                        resultFlow.emit(
                            component.result.value.orNull()
                        )
                    }
                }
            }

            override val events: Flow<Void>
                get() = component.events
            override val result: StateFlow<B?>
                get() = resultFlow
        }
    }
}