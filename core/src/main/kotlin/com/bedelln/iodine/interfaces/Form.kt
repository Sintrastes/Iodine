package com.bedelln.iodine.interfaces

import androidx.compose.runtime.*
import arrow.core.ValidatedNel
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

typealias ValidatedForm<E, A, B> = @Composable (A) -> ValidatedNel<E, B>
typealias NullableForm<A, B> = @Composable (A) -> B?

interface Form<A, B> {
    @Composable
    fun contents(initialValue: A): B

    @Composable
    operator fun invoke(p1: A): B {
        return contents(p1)
    }
}
typealias SForm<A> = Form<A, A>

/************************* Core utilities ******************************/

/**
 * Convert a [Form] into a [Component], ignoring the additional structure of the form.
 *
 * This is an explicit cast rather than a subtyping relationship to avoid
 *  ambiguity between utility functions of the same name operating on forms
 *  and components.
 */
fun <A,B> Form<A,B>.asComponent(): Component<Unit, Void, A> {
    val form = this
    return object: Component<Unit, Void,A> {
        @Composable
        override fun contents(initialValue: A) {
            form(initialValue)
        }

        override val impl = Unit
        override val events: Flow<Void> = flowOf()
    }
}

// TODO: imap
