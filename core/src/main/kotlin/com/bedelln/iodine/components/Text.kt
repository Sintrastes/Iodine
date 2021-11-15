package com.bedelln.iodine.components

import androidx.compose.material.Text
import com.bedelln.iodine.interfaces.ComposeForm
import com.bedelln.iodine.interfaces.IodineContext

/**
 * Iodine form for a (non-user editable) text display.
 */
fun Text(text: String) = ComposeForm<IodineContext,String> {
    Text(text)
}