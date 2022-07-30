package com.bedelln.iodine.components

import androidx.compose.material.Checkbox
import androidx.compose.runtime.*
import com.bedelln.iodine.interfaces.SForm

/** A simple Iodine form for entering in a boolean value via a checkbox. */
class Checkbox : SForm<Boolean> {
    @Composable
    override fun contents(initialValue: Boolean): Boolean {
        var checked by remember { mutableStateOf(initialValue) }
        Checkbox(checked,null)
        return checked
    }
}