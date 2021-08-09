package com.bedelln.iodine.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.Displayable
import com.bedelln.iodine.*
import com.bedelln.iodine.ComponentDescription
import com.bedelln.iodine.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class MultiSelectGroup<A: Displayable<C>, C: IodineContext>(
    val values: List<A>
): ComponentDescription<C, MultiSelectGroup.Event, MultiSelectGroup.Event, Set<A>, Set<A>> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Set<A>): Component<Event, Event, Set<A>, Set<A>> {
        return object: Component<Event, Event, Set<A>, Set<A>> {

            private val contentsFlow = MutableStateFlow(initialValue)

            @Composable
            override fun contents() {
                var selectedItems by remember { mutableStateOf(initialValue) }
                Column {
                    for (i in values.indices) {
                        val value = values[i]
                        Row {
                            Checkbox(
                                checked = selectedItems.contains(value),
                                onCheckedChange = {
                                    selectedItems += values[i]
                                    ctx.defaultScope.launch {
                                        contentsFlow.emit(selectedItems)
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

            override fun onEvent(event: Event) {

            }

            override val events: Flow<Event>
                get() = emptyFlow()
            override val result: StateFlow<Set<A>>
                get() = contentsFlow
        }
    }

    sealed class Event {
        data class SelectItem<A>(val item: A, val index: Int): Event()
    }
}