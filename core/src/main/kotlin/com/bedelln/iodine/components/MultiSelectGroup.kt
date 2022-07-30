package com.bedelln.iodine.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * An Iodine component used to select multiple values from a set of values of type A
 *  using a multiselect group.
 */
/*
class MultiSelectGroup<A: Displayable<C>, C: IodineContext>(
    val values: List<A>
): ComponentDescription<C, MultiSelectGroup.Action<A>, Void, Set<A>> {

    interface Action<A> {
        fun currentlySelected(): Set<A>
        fun toggle(value: A)
    }

    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Set<A>): Component<Action<A>, Void, Set<A>> {
        return object: ComponentImpl<Action<A>, Void, Set<A>, Set<A>> {
            val currentlySelected = MutableStateFlow(initialValue)
            override val state = currentlySelected

            @Composable
            override fun contents(state: Set<A>) {
                Column {
                    for (i in values.indices) {
                        val value = values[i]
                        Row {
                            Checkbox(
                                checked = state.contains(value),
                                onCheckedChange = {
                                    ctx.defaultScope.launch {
                                        currentlySelected.emit(state + values[i])
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

            override val impl: Action<A> = object: Action<A> {
                override fun currentlySelected(): Set<A> {
                    return currentlySelected.value
                }

                override fun toggle(value: A) {
                    val state = currentlySelected.value
                    if (state.contains(value)) {
                        ctx.defaultScope.launch {
                            currentlySelected.emit(state - value)
                        }
                    } else {
                        ctx.defaultScope.launch {
                            currentlySelected.emit(state + value)
                        }
                    }
                }
            }

            override val events: Flow<Void>
                get() = emptyFlow()
        }
    }
}
 */