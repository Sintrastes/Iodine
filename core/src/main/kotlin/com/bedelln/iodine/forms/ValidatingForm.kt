package com.bedelln.iodine.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import arrow.core.Either
import com.bedelln.iodine.interfaces.*
import com.bedelln.iodine.store.StoreForm
import kotlinx.coroutines.flow.*

/** A special type of store component to facilitate developing components with validation. */
abstract class ValidatingForm<C: IodineContext,I,E,A,B,Err>(
    val initialValue: A
): StoreForm<I, E, A, Either<Err, B>>() {
    val errorFlow
        get() = MutableStateFlow(result.value.swap().orNull())

    protected val mutInput
        = MutableStateFlow(initialValue)

    override val state = mutInput

    @Composable
    abstract fun contents(error: Err?, contents: A)

    @Composable
    override fun contents(state: A) {
        val errorState = errorFlow.collectAsState()
        val error by remember { errorState }
        contents(error, state)
    }
}

typealias ValidatingFormDescription<C,I,E,A,B,Err>
    = Description<C, A, ValidatingForm<C, I, E, A, B, Err>>