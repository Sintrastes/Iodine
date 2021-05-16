package com.bedelln.iodine.components.text

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
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
                        TODO("Not yet implemented")
                    }

                    @Composable
                    override fun contents() {
                        val errorState = showingError.collectAsState()
                        val contentsState = textContents.collectAsState()
                        val showingError by remember { errorState }
                        val contents by remember { contentsState }
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
                                color = Color.Red
                            )
                        }
                    }

                    fun getResults(err: Boolean, contents: String) =
                        if(err) {
                            Either.Left(Pair(InvalidInteger, contents))
                        } else {
                            Either.Right(contents.toInt())
                        }

                    override fun onEvent(event: Void) { }
                    override val events: Flow<Void>
                        get() = emptyFlow()
                    override val result: StateFlow<Either<Pair<InvalidInteger, String>, Int>>
                        get() = showingError.combine(textContents) { err, contents ->
                            getResults(err, contents)
                        }.stateIn(
                            ctx.defaultScope,
                            Lazily,
                            getResults(showingError.value, textContents.value)
                        )
                }
        }
    )
)
