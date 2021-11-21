package com.bedelln.iodine.components

import androidx.compose.material.Checkbox
import com.bedelln.iodine.interfaces.ComposeForm
import com.bedelln.iodine.interfaces.IodineContext
import com.bedelln.iodine.interfaces.SFormDescription

/** A simple Iodine form for entering in a boolean value via a checkbox. */
class Checkbox : SFormDescription<IodineContext, Unit, Void, Boolean> by (
    ComposeForm { checked ->
        Checkbox(checked,null)
    }
)