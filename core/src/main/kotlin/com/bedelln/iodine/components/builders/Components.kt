package com.bedelln.iodine.components.builders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
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

sealed class IodineMonad<C : IodineContext, out A> {
    abstract fun execute(
        ctx: C,
        childComponents: MutableList<ComponentDescription<C, Any?, Any?, Unit>>
    ): A

    data class Return<C : IodineContext, A>(val value: A) : IodineMonad<C, A>() {
        override fun execute(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, Any?, Any?, Unit>>
        ): A {
            return value
        }
    }

    data class Bind<C : IodineContext, A>(
        val action: IodineMonadF<C, IodineMonad<C, A>>
    ) : IodineMonad<C, A>() {
        override fun execute(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, Any?, Any?, Unit>>
        ): A {
            when (action) {
                is IodineMonadF.Add -> {
                    val component = action.child.initialize(ctx, Unit)
                    val componentDescr = object :
                        Description<C, Any?, Component<Any?, Any?, Unit>> {
                        @Composable
                        override fun initCompose(ctx: C) {
                            action.child.initCompose(ctx)
                        }

                        override fun initialize(
                            ctx: C,
                            initialValue: Any?
                        ): Component<Any?, Any?, Unit> {
                            return component
                        }
                    }
                    childComponents.add(componentDescr)
                    return action.rest(component)
                        .execute(ctx, childComponents)
                }
            }
        }
    }
}

sealed class IodineMonadF<C : IodineContext, A> {
    data class Add<C : IodineContext, A>(
        val child: ComponentDescription<C, Any?, Any?, Unit>,
        val rest: (Component<Any?, Any?, Unit>) -> A
    ) : IodineMonadF<C, A>()
}

interface ColumnCtx: IodineContext, ColumnScope {

    abstract class ColumnEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<ComponentDescription<C, *, *, Unit>>
    ) : Effect<IodineMonad<C, A>> {
        suspend fun <X> IodineMonad<C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <I, E> ComponentDescription<C, I, E, Unit>.not(
        ): IodineMonad<C, Component<I, E, Unit>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as Component<I, E, Unit>
                )
            }
        )

        suspend operator fun <I, E> ComponentDescription<C, I, E, Unit>.unaryMinus()
            = this.not().bind()
    }

    companion object {
        operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, Any?, Any?, Unit>>,
            func: suspend ColumnEffect<C, *>.() -> A
        ): IodineMonad<C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : ColumnEffect<C, A>(ctx, childComponents) {
                        override fun control(): DelimitedScope<IodineMonad<C, A>> {
                            return scope
                        }
                    }
                },
                f = func,
                just = { IodineMonad.Return(it) }
            )
    }
}


interface RowCtx {

    abstract class ColumnEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<ComponentDescription<C, Any?, Any?, Unit>>
    ) : Effect<IodineMonad<C, A>> {
        suspend fun <X> IodineMonad<C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <I, E> ComponentDescription<C, I, E, Unit>.not(
        ): IodineMonad<C, ComponentDescription<C, I, E, Unit>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as ComponentDescription<C, I, E, Unit>
                )
            }
        )
    }

    companion object {
        operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, *, *, Unit>>,
            func: suspend ColumnEffect<C, *>.() -> IodineMonad<C, A>
        ): IodineMonad<C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : ColumnEffect<C, A>(ctx, childComponents) {
                        override fun control(): DelimitedScope<IodineMonad<C, A>> {
                            return scope
                        }
                    }
                },
                f = func,
                just = { it }
            )
    }
}

class Column<C : IodineContext> constructor(
    val childComponents: (C) -> List<ComponentDescription<C, Any?, Any?, Unit>>,
    val modifier: Modifier,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
) : ComponentDescription<C, Unit, Void, Unit> {
    var childComponents_: List<ComponentDescription<C, Any?, Any?, Unit>>? = null

    @Composable
    override fun initCompose(ctx: C) {
        println("Calling init compose.")
        if (childComponents_ == null) {
            childComponents_ = childComponents(ctx)
        }
        childComponents_!!.forEach {
            it.initCompose(ctx)
        }

    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Unit, Void, Unit> {
        return object : ComponentImpl<Unit, Void, Unit, Unit> {

            lateinit var children: List<ComponentImpl<Unit, Void, Unit, Unit>>

            fun initChildren() {
                if (childComponents_ == null) {
                    childComponents_ = childComponents(ctx)
                }
                childComponents_!!.map {
                    it.initialize(ctx, Unit)
                }
            }

            @Composable
            override fun contents(state: Unit) {
                androidx.compose.foundation.layout.Column(
                    modifier,
                    verticalArrangement,
                    horizontalAlignment
                ) {
                    initChildren()

                    children.forEach { child ->
                        child.getContents()
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
    val childComponents: List<ComponentDescription<C, Any?, Any?, Unit>>,
    val modifier: Modifier,
    val horizontalArrangement: Arrangement.Horizontal,
    val verticalAlignment: Alignment.Vertical,
) : ComponentDescription<C, Unit, Void, Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        childComponents.forEach {
            it.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Unit, Void, Unit> {
        return object : ComponentImpl<Unit, Void, Unit, Unit> {

            val children = childComponents.map {
                it.initialize(ctx, Unit)
            }

            @Composable
            override fun contents(state: Unit) {
                val action = this
                androidx.compose.foundation.layout.Row(
                    modifier,
                    horizontalArrangement,
                    verticalAlignment
                ) {
                    children.forEach { child ->
                        child.getContents()
                    }
                }
            }

            override val events: Flow<Void>
                get() = emptyFlow()
            override val state: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
            override val impl: Unit = Unit
        }
    }
}

fun <C : IodineContext> Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    func: suspend ColumnCtx.ColumnEffect<C, *>.() -> Any?
): Column<C> {

    return Column(
        { ctx ->
            val childComponents = mutableListOf<ComponentDescription<C, Any?, Any?, Unit>>()
            ColumnCtx(ctx, childComponents, func)
                .execute(ctx, childComponents)
            childComponents
        },
        modifier,
        verticalArrangement,
        horizontalAlignment
    )
}

/*
fun <C : IodineContext> Row(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Horizontal = Arrangement.Start,
    horizontalAlignment: Alignment.Vertical = Alignment.Top,
    func: suspend ColumnCtx.ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, *>
): Row<C> {
    return Row(
        {
            val childComponents = mutableListOf<ComponentDescription<C, Any?, Any?, Unit>>()
            ColumnCtx(ctx, childComponents, func)
                .execute(childComponents)
        },
        modifier,
        verticalArrangement,
        horizontalAlignment
    )
}
 */
