package com.bedelln.iodine.components.builders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import com.bedelln.iodine.*
import com.bedelln.iodine.interfaces.Component
import com.bedelln.iodine.interfaces.ComponentDescription
import com.bedelln.iodine.interfaces.Description
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

/*
interface IMCtx
sealed class IodineMonad<Cm : IMCtx, C : IodineContext, out A> {
    abstract fun execute(ctx: C, childComponents: MutableList<ComponentDescription<C, *, *, Any?>>): A

    data class Return<Cm : IMCtx, C : IodineContext, A>(val value: A) : IodineMonad<Cm, C, A>() {
        override fun execute(ctx: C, childComponents: MutableList<ComponentDescription<C, *, *, Any?>>): A {
            return value
        }
    }

    data class Bind<Cm : IMCtx, C : IodineContext, A>(
        val action: IodineMonadF<Cm, C, IodineMonad<Cm, C, A>>
    ) : IodineMonad<Cm, C, A>() {
        override fun execute(ctx: C, childComponents: MutableList<ComponentDescription<C, *, *, Any?>>): A {
            when (action) {
                is IodineMonadF.Add -> {
                    val component = action.child.initialize(ctx, Unit)
                    val componentDescr = object : Description<C, Any?, Component<Any?, Unit, Any?>> {
                        @Composable
                        override fun initCompose(ctx: C) {
                            action.child.initCompose(ctx)
                        }

                        override fun initialize(
                            ctx: C,
                            initialValue: Any?
                        ): Component<Any?, Unit, Any?> {
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

sealed class IodineMonadF<Cm : IMCtx, C : IodineContext, A> {
    data class Add<Cm : IMCtx, C : IodineContext, A>(
        val child: ComponentDescription<C, *, *, Any?>,
        val rest: (Component<*, Unit, Any?>) -> A
    ) : IodineMonadF<Cm, C, A>()
}

interface ColumnCtx : IMCtx {

    abstract class ColumnEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<ComponentDescription<C, *, *, *>>
    ) : Effect<IodineMonad<ColumnCtx, C, A>> {
        suspend fun <X> IodineMonad<ColumnCtx, C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <Ei, Eo, B> ComponentDescription<C, Ei, Eo, B>.not(
        ): IodineMonad<ColumnCtx, C, Component<Ei, Eo, Unit, B>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as Component<Ei, Eo, Unit, B>
                )
            }
        )
    }

    companion object {
        operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, *, *, *>>,
            func: suspend ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, A>
        ): IodineMonad<ColumnCtx, C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : ColumnEffect<C, A>(ctx, childComponents) {
                        override fun control(): DelimitedScope<IodineMonad<ColumnCtx, C, A>> {
                            return scope
                        }
                    }
                },
                f = func,
                just = { it }
            )
    }
}


interface RowCtx : IMCtx {

    abstract class ColumnEffect<C : IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<ComponentDescription<C, *, *, *>>
    ) : Effect<IodineMonad<RowCtx, C, A>> {
        suspend fun <X> IodineMonad<RowCtx, C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <Ei, Eo, B> ComponentDescription<C, Ei, Eo, B>.not(
        ): IodineMonad<RowCtx, C, ComponentDescription<C, Ei, Eo, B>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as ComponentDescription<C, Ei, Eo, B>
                )
            }
        )
    }

    companion object {
        operator fun <A, C : IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<ComponentDescription<C, *, *, *>>,
            func: suspend ColumnEffect<C, *>.() -> IodineMonad<RowCtx, C, A>
        ): IodineMonad<RowCtx, C, A> =
            Effect.restricted(
                eff = { scope ->
                    object : ColumnEffect<C, A>(ctx, childComponents) {
                        override fun control(): DelimitedScope<IodineMonad<RowCtx, C, A>> {
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
    val childComponents: (C) -> List<ComponentDescription<C, *, *, *>>,
    val modifier: Modifier,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
) : ComponentDescription<C, Void, Void, Unit> {
    var childComponents_: List<ComponentDescription<C, *, *, *>>? = null

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

    override fun initialize(ctx: C, initialValue: Unit): Component<C, Void, Unit, Unit> {
        return object : Component<C, Void, Void, Unit> {

            val children
                get() = run {
                    if (childComponents_ == null) {
                        childComponents_ = childComponents(ctx)
                    }
                    childComponents_!!.map {
                        it.initialize(ctx, Unit)
                    }
                }

            @Composable
            override fun contents() {
                androidx.compose.foundation.layout.Column(
                    modifier,
                    verticalArrangement,
                    horizontalAlignment
                ) {
                    children.forEach { child ->
                        child.contents()
                    }
                }
            }

            override fun onEvent(event: Void) {}
            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

class Row<C : IodineContext> constructor(
    val childComponents: List<ComponentDescription<C, *, *, *>>,
    val modifier: Modifier,
    val horizontalArrangement: Arrangement.Horizontal,
    val verticalAlignment: Alignment.Vertical,
) : ComponentDescription<C, Void, Void, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        childComponents.forEach {
            it.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): Component<Void, Void, Unit, Unit> {
        return object : Component<Void, Void, Unit, Unit> {

            val children = childComponents.map {
                it.initialize(ctx, Unit)
            }

            @Composable
            override fun contents() {
                val action = this
                androidx.compose.foundation.layout.Row(
                    modifier,
                    horizontalArrangement,
                    verticalAlignment
                ) {
                    children.forEach { child ->
                        child.contents()
                    }
                }
            }

            override fun onEvent(event: Void) {}
            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

fun <C : IodineContext> Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    func: suspend ColumnCtx.ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, *>
): Column<C> {

    return Column(
        { ctx ->
            val childComponents = mutableListOf<ComponentDescription<C, *, *, Unit, *>>()
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
fun <C: IodineContext> Row(
        modifier: Modifier = Modifier,
        verticalArrangement: Arrangement.Horizontal = Arrangement.Start,
        horizontalAlignment: Alignment.Vertical = Alignment.Top,
        func: suspend ColumnCtx.ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, *>
): Row<C> {
    return Row(
        {
            val childComponents = mutableListOf<ComponentDescription<C, *, *, Unit, *>>()
            ColumnCtx(childComponents, func)
                .execute(childComponents)
        },
            modifier,
            verticalArrangement,
            horizontalAlignment
    )
}
*/

 */




