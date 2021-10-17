package com.bedelln.iodine.interfaces

import androidx.compose.runtime.Composable
import com.bedelln.iodine.util.mapStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FormViewModel<I, E, S, A, B>: ViewModel<I, E, S, A>, Gettable<B>

interface FormImpl<C,I,E,S,A,B>: ComponentImpl<C,I,E,S,A>, FormViewModel<I, E, S, A, B>

typealias FormDescriptionImpl<Ctx,I,E,S,A,B>
    = Description<Ctx, A, FormImpl<Ctx, I, E, S, A, B>>

typealias FormDescription<Ctx,I,E,A,B>
    = FormDescriptionImpl<Ctx,I,E,*,A,B>

inline fun <Ctx,I,E,S,A,B,X> FormDescriptionImpl<Ctx,I,E,S,A,B>.omap(
    crossinline f: (B) -> X
): FormDescriptionImpl<Ctx,I,E,S,A,X> {
    val origDescr = this
    return object: FormDescriptionImpl<Ctx,I,E,S,A,X> {
        override fun initialize(ctx: Ctx, initialValue: A): FormImpl<Ctx,I,E,S,A,X> {
            val orig = origDescr.initialize(ctx, initialValue)
            return object: FormImpl<Ctx,I,E,S,A,X> {
                @Composable
                override fun contents(state: S) {
                    orig.contents(state )
                }
                override val events: Flow<E>
                    get() = orig.events
                override val result: StateFlow<X>
                    get() = orig.result
                        .mapStateFlow(f)

                override fun <A> interact(action: I.(Ctx) -> A): A {
                    return orig.interact(action)
                }

                override val state: StateFlow<S>
                    get() = orig.state
            }
        }

        @Composable
        override fun initCompose(ctx: Ctx) {
            origDescr.initCompose(ctx)
        }
    }
}