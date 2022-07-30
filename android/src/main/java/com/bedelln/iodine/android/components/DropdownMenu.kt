package com.bedelln.iodine.android.components

import androidx.compose.runtime.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MenuDefaults
import androidx.compose.material.DropdownMenuItem
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/*
class DropdownMenu<C: IodineContext, A: Displayable<C>>(
    val dropdownItems: List<A>
): ComponentDescription<C, Any, DropdownMenu.Event<A>, A> {

    data class Event<A>(val selected: A)

    @Composable
    override fun initCompose(ctx: C) { }

    override fun initialize(ctx: C, initialValue: A): Component<Any, Event<A>, A> {
        return object: ComponentImpl<Unit, Event<A>, A, A> {
            val resultFlow = MutableStateFlow(initialValue)
            override val state get() = resultFlow

            @Composable
            override fun contents(state: A) {
                val flowState = resultFlow.collectAsState()
                var expandedState by remember { mutableStateOf(false) }
                val selectedItem by remember { flowState }
                Box(
                    modifier = Modifier.fillMaxSize()
                        .wrapContentSize(Alignment.TopStart)
                         // .backgrond(Color.Gray)
                        .clickable(onClick = {
                            expandedState = true
                        })
                    .padding(
                        MenuDefaults.DropdownMenuItemContentPadding
                    )
                ) {
                    with(selectedItem) {
                        ctx.display()
                    }
                    DropdownMenu(
                        expanded = expandedState,
                        onDismissRequest = {
                            expandedState = false
                        },
                        content = {
                            dropdownItems.forEachIndexed { index, item ->
                                DropdownMenuItem(onClick = {
                                    ctx.defaultScope.launch {
                                        resultFlow.emit(item)
                                    }
                                    expandedState = false
                                }) {
                                    with (item) {
                                        ctx.display()
                                    }
                                }
                            }
                        }
                    )
                }
            }

            override val events: Flow<Event<A>>
                get() = emptyFlow()
            override val impl = Unit
        }
    }
}
 */