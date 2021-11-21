package com.bedelln.iodine.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * Iodine component which displays a button with the
 *  given text, and launches the given tool when clicked.
 */
class ActionButton<C: IodineContext>(
    val text: String,
    val action: ToolDescription<C, Any, Unit, Any?>
): ComponentDescription<C, ActionButton.Action, ActionButton.Event, Unit> {

    interface Action {
        fun click()
    }

    sealed interface Event {
        object OnClick: Event
    }

    val eventImpl = MutableSharedFlow<Event>()

    // TODO: I'm not sure why this was used originally.
    // lateinit var setShow: () -> Unit

    @Composable
    override fun initCompose(ctx: C) {
        // var initialized by remember { mutableStateOf(false) }
        // setShow = { initialized = true }
        // if (initialized) {
            action.initCompose(ctx)
        // }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Action, Event, Unit> = run {
        object : ComponentImpl<Action, Event, Unit, Unit> {
            @Composable
            override fun contents(state: Unit) {
                Button(
                    content = {
                        Text(text)
                    },
                    onClick = {
                        impl.click()
                    }
                )
            }

            override val impl = object: Action {
                override fun click() {
                    ctx.defaultScope.launch {
                        action.initialize(ctx, Unit)
                            .runTool(ctx)
                    }
                    ctx.defaultScope.launch {
                        eventImpl.emit(Event.OnClick)
                    }
                }
            }

            override val events get()
                = eventImpl
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}