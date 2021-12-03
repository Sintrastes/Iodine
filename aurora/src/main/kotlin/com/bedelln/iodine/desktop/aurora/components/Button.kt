package com.bedelln.iodine.desktop.aurora.components

import com.bedelln.iodine.interfaces.Compose
import com.bedelln.iodine.interfaces.IodineContext
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.projection.CommandButtonProjection

/**
 * Iodine component for an aurora button.
 */
fun Button(text: String) = Compose<IodineContext> {
    CommandButtonProjection(
        contentModel = Command(
            text = text,
            action = {  }
        )
    ).project()
}