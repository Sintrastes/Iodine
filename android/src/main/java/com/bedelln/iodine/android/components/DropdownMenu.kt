package com.bedelln.iodine.components

import androidx.compose.runtime.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import com.bedelln.iodine.ComponentAction
import com.bedelln.iodine.HComponent
import com.bedelln.iodine.HComponentDescription
import com.bedelln.iodine.IodineContext
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.math.exp

class DropdownMenu<C: IodineContext, A: Displayable<C>>(
    val dropdownItems: List<A>
): HComponentDescription<C,DropdownMenu.Event<A>,DropdownMenu.Event<A>,Unit,Unit> {

    data class Event<A>(val selected: A)

    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: Unit): HComponent<Event<A>, Event<A>, Unit, Unit> {
        return object: HComponent<Event<A>, Event<A>, Unit, Unit> {
            @Composable
            override fun ComponentAction<Unit, Event<A>>.contents() {
                var expandedState by remember { mutableStateOf(false) }
                DropdownMenu(
                    expanded = expandedState,
                    onDismissRequest = {
                        expandedState = false
                    },
                    content = {
                        dropdownItems.forEachIndexed { index, item ->
                            DropdownMenuItem(onClick = {
                                //selectedIndex = index
                                // expanded = false
                            }) {
                                with(item) {
                                    ctx.display()
                                }
                            }
                        }
                    }
                )
            }

            override fun ComponentAction<Unit, Event<A>>.onEvent(event: Event<A>) {

            }

            override val events: Flow<Event<A>>
                get() = emptyFlow()
            override val result: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}