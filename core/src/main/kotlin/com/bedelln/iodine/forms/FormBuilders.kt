package com.bedelln.iodine.forms

import androidx.compose.runtime.*
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.andThen
import arrow.optics.Lens
import com.bedelln.iodine.interfaces.Form
import com.bedelln.iodine.interfaces.NullableForm
import com.bedelln.iodine.interfaces.ValidatedForm

interface FormBuilder<S> {
    @Composable
    fun <A> Form<A, A>.bind(field: Lens<S, A>): A
}

fun <S> form(builder: @Composable FormBuilder<S>.() -> Unit): Form<S, S> = Form { initialValue ->
    val updaters = mutableStateListOf<(S) -> S>()
    var state by remember { mutableStateOf(initialValue) }

    val ctx = object : FormBuilder<S> {
        @Composable
        override fun <A> (Form<A, A>).bind(field: Lens<S, A>): A {
            val formValue = this(field.get(state))
            updaters += { x -> field.set(x, formValue) }
            return formValue
        }
    }

    ctx.builder()

    val updater = updaters.foldRight({ it: S -> it }) { f, g ->
        { x -> f(g(x)) }
    }

    state = updater(initialValue)

    state
}

interface NullableFormBuilder<S> {
    @Composable
    fun <A> NullableForm<A, A>.bind(field: Lens<S, A>): A?
}

fun <S> nullableForm(
    builder: @Composable NullableFormBuilder<S>.() -> Unit
): NullableForm<S, S> = @Composable { initialValue ->
    val updaters = mutableStateListOf<(S) -> S?>()
    var state by remember { mutableStateOf<S?>(initialValue) }

    val ctx = object : NullableFormBuilder<S> {
        @Composable
        override fun <A> (NullableForm<A, A>).bind(field: Lens<S, A>): A? {
            val formValue = state?.let { this(field.get(it)) }
            updaters += { x -> formValue?.let { field.set(x, it) } }
            return formValue
        }
    }

    ctx.builder()

    val updater: (S) -> S? = updaters.foldRight({ it: S -> it }) { f, g ->
        { x -> g(x)?.let { f(it) } }
    }

    state = updater(initialValue)

    state
}

interface ValidatedFormBuilder<E, S> {
    @Composable
    fun <A> ValidatedForm<E, A, A>.bind(field: Lens<S, A>): ValidatedNel<E, A>
}

fun <E, S> validatedForm(
    builder: @Composable ValidatedFormBuilder<E, S>.() -> Unit
): ValidatedForm<E, S, S> = @Composable { initialValue ->
    val updaters = mutableStateListOf<(S) -> ValidatedNel<E, S>>()
    var state by remember { mutableStateOf<ValidatedNel<E, S>>(Validated.Valid(initialValue)) }

    val ctx = object : ValidatedFormBuilder<E, S> {
        @Composable
        override fun <A> (ValidatedForm<E, A, A>).bind(field: Lens<S, A>): ValidatedNel<E, A> {
            val formValue = state.andThen { this(field.get(it)) }
            updaters += { x -> formValue.map { field.set(x, it) } }
            return formValue
        }
    }

    ctx.builder()

    val updater: (S) -> ValidatedNel<E, S> = updaters.foldRight({ it: S -> Validated.Valid(it) }) { f, g ->
        { x -> g(x).andThen { f(it) } }
    }

    state = updater(initialValue)

    state
}
