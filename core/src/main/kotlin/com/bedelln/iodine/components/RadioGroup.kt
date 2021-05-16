package com.bedelln.iodine.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.Displayable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RadioGroup<A: Displayable<C>, C: IodineContext>(
    val values: List<A>
): HComponentDescription<C, RadioGroup.Event, RadioGroup.Event, A?, A?> {
    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: A?): HComponent<Event, Event, A?, A?> {
        return object: HComponent<Event, Event, A?, A?> {

            private val contentsFlow = MutableStateFlow(initialValue)

            @Composable
            override fun contents() {
                var selected by remember { mutableStateOf(initialValue) }
                Column {
                    for (i in values.indices) {
                        val value = values[i]
                        Row {
                            RadioButton(
                                selected = value == selected,
                                onClick = {
                                    selected = value
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

            override fun onEvent(event: Event) {

            }

            override val events: Flow<Event>
                get() = emptyFlow()
            override val result: StateFlow<A?>
                get() = contentsFlow
        }
    }

    sealed class Event {
        data class SelectItem<A>(val item: A, val index: Int): Event()
    }
}