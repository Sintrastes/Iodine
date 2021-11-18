package com.bedelln.iodine.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.material.*
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RadioGroup<A: Displayable<C>, C: IodineContext>(
    val values: List<A>
): ComponentDescription<C, RadioGroup.Action<A>, Void, A?> {
    interface Action<A> {
        fun select(value: A)
        fun currentlySelected(): A?
    }

    override fun initialize(ctx: C, initialValue: A?): Component<Action<A>, Void, A?> {
        return object: ComponentImpl<Action<A>, Void, A?, A?> {

            val contentsFlow = MutableStateFlow(initialValue)
            override val state = contentsFlow

            @Composable
            override fun contents(state: A?) {
                Column {
                    for (i in values.indices) {
                        val value = values[i]
                        Row {
                            RadioButton(
                                selected = value == state,
                                onClick = {
                                    ctx.defaultScope.launch {
                                        contentsFlow.emit(value)
                                    }
                                }
                            )
                            with(value) {
                                ctx.display()
                            }
                        }
                    }
                }
            }

            override val impl = object: Action<A> {
                override fun select(value: A) {
                    ctx.defaultScope.launch {
                        contentsFlow.emit(value)
                    }
                }

                override fun currentlySelected(): A? {
                    return contentsFlow.value
                }
            }

            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}