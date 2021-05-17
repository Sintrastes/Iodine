package com.bedelln.iodine.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import arrow.core.Either
import com.bedelln.iodine.components.ValidatedComponent
import com.bedelln.iodine.components.ValidationEvent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.SettableHComponent
import com.bedelln.iodine.interfaces.SettableHComponentDescription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.launch

object InvalidInteger

/** A component for inputting integers. */
class IntEntry<C : IodineContext> : HComponentDescription<C, ValidationEvent<Void>, Void, String, Int?> by (
    ValidatedComponent<C, Void, Void, String, Int, InvalidInteger>(
        object :
            SettableHComponentDescription<
                C, Void, Void,
                Pair<InvalidInteger?, String>,
                Either<Pair<InvalidInteger, String>, Int>
                > {
            @Composable
            override fun initCompose(ctx: C) { }

            override fun initialize(ctx: C, initialValue: Pair<InvalidInteger?, String>) =
                object : SettableHComponent<C, Void, Void, Pair<InvalidInteger?, String>, Either<Pair<InvalidInteger, String>, Int>> {

                    val showingError = MutableStateFlow(
                        initialValue.first != null
                    )
                    val textContents = MutableStateFlow(
                        initialValue.second
                    )

                    override fun C.setValue(newValue: Pair<InvalidInteger?, String>) {
                        ctx.defaultScope.launch {
                            if (newValue.first != null) {
                                showingError.emit(true)
                            } else {
                                showingError.emit(false)
                            }
                        }
                    }

                    @Composable
                    override fun contents() {
                        val errorState = showingError.collectAsState()
                        val contentsState = textContents.collectAsState()
                        val showingError by remember { errorState }
                        val contents by remember { contentsState }
                        Column {
                            TextField(
                                value = contents,
                                onValueChange = { newValue ->
                                    ctx.defaultScope.launch {
                                        textContents.emit(newValue)
                                    }
                                },
                                label = {
                                    Text("")
                                }
                            )
                            if (showingError) {
                                Text(
                                    text = "Not a valid integer",
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    fun getResults(contents: String) =
                        contents.toIntOrNull()?.let {
                            Either.Right(it)
                        }
                            ?: Either.Left(Pair(InvalidInteger, contents))


                    override fun onEvent(event: Void) { }
                    override val events: Flow<Void>
                        get() = emptyFlow()
                    override val result: StateFlow<Either<Pair<InvalidInteger, String>, Int>>
                        get() = textContents.map { contents ->
                            getResults(contents)
                        }.stateIn(
                            ctx.defaultScope,
                            Lazily,
                            getResults(textContents.value)
                        )
                }
        }
    )
)
