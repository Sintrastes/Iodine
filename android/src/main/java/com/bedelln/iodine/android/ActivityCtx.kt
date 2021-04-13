package com.bedelln.iodine.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.bedelln.iodine.*

interface ActivityCtx : AndroidCtx {
    val activityCtx: Context
}

abstract class IodineActivity<C,E,A,B>(val initialValue: A): ComponentActivity() {
    abstract val contents: ComponentDescription<C, E, A, B>

    abstract val contextInitializer: (ActivityCtx) -> C

    private val activity = this

    val ctx = object : ActivityCtx {
        override val activityCtx = activity
        override val defaultScope = activity.lifecycleScope
        override val defaultCtx = activityCtx
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        val _contents = contents.initialize(
            contextInitializer(ctx),
            initialValue
        )
        setContent {
            MaterialTheme {
                _contents.contents()
            }
        }
    }
}

