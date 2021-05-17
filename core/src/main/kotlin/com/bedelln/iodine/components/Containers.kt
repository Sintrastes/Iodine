package com.bedelln.iodine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import com.bedelln.iodine.*
import com.bedelln.iodine.interfaces.Description
import com.bedelln.iodine.interfaces.HComponent
import com.bedelln.iodine.interfaces.HComponentDescription
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.util.function.Consumer

// Note: Currently I can't directly mirror
// what Compose can do here with Columns.
// But I may be able to emulate this with a
// monadic interface making use of arrow.

interface IMCtx
sealed class IodineMonad<Cm: IMCtx, C: IodineContext, out A> {
    abstract fun execute(ctx: C, childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>): A

    data class Return<Cm: IMCtx, C: IodineContext, A>(val value: A): IodineMonad<Cm, C, A>() {
        override fun execute(ctx: C, childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>): A {
            return value
        }
    }

    data class Bind<Cm: IMCtx, C: IodineContext, A>(
        val action: IodineMonadF<Cm, C, IodineMonad<Cm,C,A>>
    ): IodineMonad<Cm,C,A>() {
        override fun execute(ctx: C, childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>): A {
            when (action) {
                is IodineMonadF.Add -> {
                    val component = action.child.initialize(ctx, Unit)
                    val componentDescr = object: Description<C,Any?, HComponent<Nothing, Any?, Unit, Any?>> {
                        @Composable
                        override fun initCompose(ctx: C) {
                            action.child.initCompose(ctx)
                        }

                        override fun initialize(
                            ctx: C,
                            initialValue: Any?
                        ): HComponent<Nothing, Any?, Unit, Any?> {
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

sealed class IodineMonadF<Cm: IMCtx, C: IodineContext, A> {
    data class Add<Cm: IMCtx, C: IodineContext, A>(
            val child: HComponentDescription<C, *, *, Unit, *>,
            val rest: (HComponent<*,*,Unit,*>) -> A
    ): IodineMonadF<Cm, C, A>()
}

interface ColumnCtx: IMCtx {

    abstract class ColumnEffect<C: IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>
    ): Effect<IodineMonad<ColumnCtx, C, A>> {
        suspend fun <X> IodineMonad<ColumnCtx, C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <Ei, Eo, B> HComponentDescription<C, Ei, Eo, Unit, B>.not(
        ): IodineMonad<ColumnCtx,C, HComponent<Ei, Eo, Unit, B>> = IodineMonad.Bind(
            IodineMonadF.Add(this) {
                IodineMonad.Return(
                    it as HComponent<Ei, Eo, Unit, B>
                )
            }
        )
    }

    companion object {
        operator fun <A,C: IodineContext> invoke(
                ctx: C,
                childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>,
                func: suspend ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, A>
        ): IodineMonad<ColumnCtx, C, A> =
            Effect.restricted(
                eff = { scope ->
                    object: ColumnEffect<C,A>(ctx,childComponents) {
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


interface RowCtx: IMCtx {

    abstract class ColumnEffect<C: IodineContext, A>(
        val ctx: C,
        val childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>
    ): Effect<IodineMonad<RowCtx, C, A>> {
        suspend fun <X> IodineMonad<RowCtx, C, X>.bind(): X = run {
            this.execute(ctx, childComponents)
        }

        operator fun <Ei, Eo, B> HComponentDescription<C, Ei, Eo, Unit, B>.not(
        ): IodineMonad<RowCtx,C, HComponentDescription<C, Ei, Eo, Unit, B>> = IodineMonad.Bind(
                IodineMonadF.Add(this) {
                    IodineMonad.Return(
                            it as HComponentDescription<C, Ei, Eo, Unit, B>
                    )
                }
        )
    }

    companion object {
        operator fun <A,C: IodineContext> invoke(
            ctx: C,
            childComponents: MutableList<HComponentDescription<C, *, *, Unit, *>>,
            func: suspend ColumnEffect<C, *>.() -> IodineMonad<RowCtx, C, A>
        ): IodineMonad<RowCtx, C, A> =
            Effect.restricted(
                eff = { scope ->
                    object: ColumnEffect<C,A>(ctx, childComponents) {
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

class Column<C: IodineContext> constructor(
        val childComponents: (C) -> List<HComponentDescription<C, *, *, Unit, *>>,
        val modifier: Modifier,
        val verticalArrangement: Arrangement.Vertical,
        val horizontalAlignment: Alignment.Horizontal,
): HComponentDescription<C, Void, Void, Unit, Unit> {
    var childComponents_: List<HComponentDescription<C,*,*,Unit,*>>? = null
    @Composable
    override fun initCompose(ctx: C) {
        println("Calling init compose.")
        if(childComponents_ == null) {
            childComponents_ = childComponents(ctx)
        }
        childComponents_!!.forEach {
            it.initCompose(ctx)
        }

    }

    override fun initialize(ctx: C, initialValue: Unit): HComponent<Void, Void, Unit, Unit> {
        return object: HComponent<Void, Void, Unit, Unit> {

            val children get() = run {
                if(childComponents_ == null) {
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

            override fun onEvent(event: Void) { }
            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

class Row<C: IodineContext> constructor(
        val childComponents: List<HComponentDescription<C, *, *, Unit, *>>,
        val modifier: Modifier,
        val horizontalArrangement: Arrangement.Horizontal,
        val verticalAlignment: Alignment.Vertical,
): HComponentDescription<C, Void, Void, Unit, Unit> {
    @Composable
    override fun initCompose(ctx: C) {
        childComponents.forEach {
            it.initCompose(ctx)
        }
    }

    override fun initialize(ctx: C, initialValue: Unit): HComponent<Void, Void, Unit, Unit> {
        return object: HComponent<Void, Void, Unit, Unit> {

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

            override fun onEvent(event: Void) { }
            override val events: Flow<Void>
                get() = emptyFlow()
            override val result: StateFlow<Unit>
                get() = MutableStateFlow(Unit)
        }
    }
}

fun <C: IodineContext> Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    func: suspend ColumnCtx.ColumnEffect<C, *>.() -> IodineMonad<ColumnCtx, C, *>
): Column<C> {

    return Column(
        { ctx ->
            val childComponents = mutableListOf<HComponentDescription<C, *, *, Unit, *>>()
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
            val childComponents = mutableListOf<HComponentDescription<C, *, *, Unit, *>>()
            ColumnCtx(childComponents, func)
                .execute(childComponents)
        },
            modifier,
            verticalArrangement,
            horizontalAlignment
    )
}
*/




