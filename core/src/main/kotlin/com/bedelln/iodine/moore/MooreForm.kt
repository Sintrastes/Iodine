package com.bedelln.iodine.moore

import androidx.compose.runtime.Composable
import com.bedelln.iodine.interfaces.IodineContext
import kotlinx.coroutines.flow.StateFlow

abstract class MooreFormImpl<C: IodineContext,Ei,Eo,S,A,B>(
    initialState: S
): MooreComponentImpl<C, Ei, Eo, S, A>(initialState)

