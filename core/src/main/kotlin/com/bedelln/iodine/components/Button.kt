package com.bedelln.iodine.components

import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.ComponentImpl
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Iodine interface for a simple button.
 */
class Button(
    val text: String,
    val modifier: Modifier = Modifier
): ComponentDescription<IodineContext, Button.Action, Button.Event, Unit> {
    interface Action {
        fun click()
    }

    sealed interface Event {
        object OnClick: Event
    }

    override fun initialize(
        ctx: IodineContext,
        initialValue: Unit
    ): ComponentImpl<Action, Event, *, Unit> {
        return object: ComponentImpl<Action, Event, Unit, Unit> {

            @Composable
            override fun contents(state: Unit) {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        impl.click()
                    },
                    modifier = modifier
                )
            }

            override val impl = object: Action {
                override fun click() {
                    ctx.defaultScope.launch {
                        eventsImpl.emit(Event.OnClick)
                    }
                }
            }
            override val events: Flow<Event>
                get() = eventsImpl
            val eventsImpl
                = MutableSharedFlow<Event>()

            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}