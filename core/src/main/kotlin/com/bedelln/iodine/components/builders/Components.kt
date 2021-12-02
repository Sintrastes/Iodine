package com.bedelln.iodine.components.builders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import com.bedelln.iodine.interfaces.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

internal sealed class IodineMonad<C : IodineContext, out A> {
    abstract fun execute(
        ctx: C,
        childComponents: MutableList<PComponentDescription<C, Any?, Any?, Unit, *>>,
        childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *, *>>
    ): A

    internal data class Return<C : IodineContext, A>(val value: A) : IodineMonad<C, A>() {
        override fun execute(
            ctx: C,
            childComponents: MutableList<PComponentDescription<C, Any?, Any?, Unit, *>>,
            childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *,*>>
        ): A {
            return value
        }
    }

    internal data class Bind<C : IodineContext, A>(
        val action: IodineMonadF<C, IodineMonad<C, A>>
    ) : IodineMonad<C, A>() {
        override fun execute(
            ctx: C,
            childComponents: MutableList<PComponentDescription<C, Any?, Any?, Unit, *>>,
            childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *,*>>
        ): A {
            when (action) {
                is IodineMonadF.Add -> {
                    val component = action.child.initialize(ctx, Unit)
                    val componentDescr = object :
                        Description<C, Unit, Component<Any?, Any?, *>> {
                        @Composable
                        override fun initCompose(ctx: C) {
                            action.child.initCompose(ctx)
                        }

                        override fun initialize(
                            ctx: C,
                            initialValue: Unit
                        ): Component<Any?, Any?, *> {
                            return component
                        }
                    }
                    childComponents.add(componentDescr)
                    return action.rest(component)
                        .execute(ctx, childComponents, childForms)
                }
                is IodineMonadF.AddForm -> {
                    TODO()
                }
            }
        }
    }
}

sealed class IodineMonadF<C : IodineContext, A> {
    data class Add<C : IodineContext, A>(
        val child: PComponentDescription<C, Any?, Any?, Unit, *>,
        val rest: (Component<Any?, Any?, *>) -> A
    ) : IodineMonadF<C, A>()

    data class AddForm<C : IodineContext, A>(
        val child: PFormDescription<C, Any?, Any?, Unit, *, *>,
        val rest: (Form<Any?, Any?, *, *>) -> A
    ) : IodineMonadF<C, A>()
}

interface ColumnCtx : IodineContext, ColumnScope {

    abstract class ColumnEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<PComponentDescription<C, *, *, Unit, *>>,
        val childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *,*>>
    ) : Effect<IodineMonad<C, A>> {
        internal fun <X> IodineMonad<C, X>.bind(): X = run {
            this.execute(ctx, childComponents, childForms)
        }

        internal operator fun <I, E, A> PComponentDescription<C, I, E, Unit, A>.not(
        ): IodineMonad<C, Component<I, E, Unit>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as Component<I, E, Unit>
                )
            }
        )

        @JvmName("notForm")
        internal operator fun <I, E, A, B> PFormDescription<C, I, E, Unit, A, B>.not(
        ): IodineMonad<C, Form<I, E, Unit, A>> = IodineMonad.Bind(
            IodineMonadF.AddForm(this) {
                IodineMonad.Return(
                    it as Form<I, E, Unit, A>
                )
            }
        )

        operator fun <I, E> ComponentDescription<C, I, E, Unit>.unaryMinus() =
            this.not().bind()

        operator fun <I, E, A> FormDescription<C, I, E, Unit, A>.unaryMinus() =
            this.not().bind()
    }

    companion object {
        @Composable
        internal operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<PComponentDescription<C, Any?, Any?, Unit, *>>,
            childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *,*>>,
            func: @Composable ColumnEffect<C, *>.() -> A
        ): IodineMonad<C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : ColumnEffect<C, A>(ctx, childComponents, childForms) {
                        override fun control(): DelimitedScope<IodineMonad<C, A>> {
                            return scope
                        }
                    }
                },
                f = { func() },
                just = { IodineMonad.Return(it) }
            )
    }
}


interface RowCtx : IodineContext, RowScope {

    abstract class RowEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<PComponentDescription<C, Any?, Any?, Unit, *>>,
        val childForms: MutableList<PFormDescription<C, Any?, Any?, Unit, *,*>>
    ) : Effect<IodineMonad<C, A>> {
        internal fun <X> IodineMonad<C, X>.bind(): X = run {
            this.execute(ctx, childComponents, childForms)
        }

        internal operator fun <I, E, A> PComponentDescription<C, I, E, Unit, A>.not(
        ): IodineMonad<C, Component<I, E, Unit>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as Component<I, E, Unit>
                )
            }
        )

        @JvmName("notForm")
        internal operator fun <I, E, A, B> PFormDescription<C, I, E, Unit, A, B>.not(
        ): IodineMonad<C, Form<I, E, Unit, A>> = IodineMonad.Bind(
            IodineMonadF.AddForm(this) {
                IodineMonad.Return(
                    it as Form<I, E, Unit, A>
                )
            }
        )

        operator fun <I, E, A> PComponentDescription<C, I, E, Unit, A>.unaryMinus() =
            this.not().bind()

        operator fun <I, E, A, B> PFormDescription<C, I, E, Unit, A, B>.unaryMinus() =
            this.not().bind()
    }

    companion object {
        @Composable
        internal operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<PComponentDescription<C, *, *, Unit, *>>,
            childForms: MutableList<PFormDescription<C, *, *, Unit, *, *>>,
            func: @Composable RowEffect<C, *>.() -> A
        ): IodineMonad<C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : RowEffect<C, A>(ctx, childComponents, childForms) {
                        override fun control(): DelimitedScope<IodineMonad<C, A>> {
                            return scope
                        }
                    }
                },
                f = { func() },
                just = { IodineMonad.Return(it) }
            )
    }
}

class Column<C : IodineContext> constructor(
    val childComponents: @Composable (C) -> List<PComponentDescription<C, Any?, Any?, Unit,*>>,
    val modifier: Modifier,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
) : ComponentDescription<C, Unit, Void, Unit> {
    lateinit var childComponents_: List<PComponentDescription<C, Any?, Any?, Unit, *>>

    @Composable
    override fun initCompose(ctx: C) {
        childComponents_ = childComponents(ctx)

        childComponents_.forEach {
            it.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Unit, Void, Unit> {
        return object : ComponentImpl<Unit, Void, Unit, Unit> {

            @Composable
            override fun contents(state: Unit) {
                androidx.compose.foundation.layout.Column(
                    modifier,
                    verticalArrangement,
                    horizontalAlignment
                ) {
                    childComponents_.forEach { child ->
                        child.getContents(ctx, Unit)
                    }
                }
            }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val impl = Unit
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

class Row<C : IodineContext> constructor(
    val childComponents: @Composable (C) -> List<PComponentDescription<C, Any?, Any?, Unit,*>>,
    val modifier: Modifier,
    val horizontalArrangement: Arrangement.Horizontal,
    val verticalAlignment: Alignment.Vertical
) : ComponentDescription<C, Unit, Void, Unit> {
    lateinit var childComponents_: List<PComponentDescription<C, Any?, Any?, Unit,*>>

    @Composable
    override fun initCompose(ctx: C) {
        childComponents_ = childComponents(ctx)

        childComponents_.forEach {
            it.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Unit, Void, Unit> {
        return object : ComponentImpl<Unit, Void, Unit, Unit> {

            @Composable
            override fun contents(state: Unit) {
                androidx.compose.foundation.layout.Row(
                    modifier,
                    horizontalArrangement,
                    verticalAlignment
                ) {
                    childComponents_.forEach { child ->
                        child.getContents(ctx, Unit)
                    }
                }
            }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val impl = Unit
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

fun <C : IodineContext> Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    func: @Composable ColumnCtx.ColumnEffect<C, *>.() -> Any?
): Column<C> {

    return Column(
        { ctx ->
            val childComponents = mutableListOf<PComponentDescription<C, Any?, Any?, Unit, *>>()
            val childForms = mutableListOf<PFormDescription<C, Any?, Any?, Unit, *,*>>()
            ColumnCtx(ctx, childComponents, childForms, func)
                .execute(ctx, childComponents, childForms)
            childComponents
        },
        modifier,
        verticalArrangement,
        horizontalAlignment
    )
}

fun <C : IodineContext> Row(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Horizontal = Arrangement.Start,
    horizontalAlignment: Alignment.Vertical = Alignment.Top,
    func: @Composable RowCtx.RowEffect<C, *>.() -> Any?
): Row<C> {
    return Row(
        { ctx ->
            val childComponents = mutableListOf<PComponentDescription<C, Any?, Any?, Unit, *>>()
            val childForms = mutableListOf<PFormDescription<C, Any?, Any?, Unit, *,*>>()
            RowCtx(ctx, childComponents, childForms, func)
                .execute(ctx, childComponents, childForms)
            childComponents
        },
        modifier,
        verticalArrangement,
        horizontalAlignment
    )
}
