package com.bedelln.iodine.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.bedelln.iodine.*

interface ActivityCtx : AndroidCtx, HasRef {
    val activityCtx: Context
}

abstract class IodineActivity<C,E,A,B>(val initialValue: A): ComponentActivity() {
    abstract val contents: ComponentDescription<C, E, A, B>

    abstract val contextInitializer: (ActivityCtx) -> C

    private val activity = this

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContent {
            var additionalContents by remember {
                mutableStateOf(listOf<@Composable() () -> Unit>())
            }
            val ctx = object : ActivityCtx {
                override val activityCtx = activity
                override val defaultScope = activity.lifecycleScope
                override val ref: ContainerRef = object: ContainerRef {
                    override fun addToContents(f: @Composable() () -> Unit) {
                        additionalContents = additionalContents + listOf(f)
                    }
                }
                override val defaultCtx = activityCtx
            }
            val _contents = contents.initialize(
                contextInitializer(ctx),
                initialValue
            )
            MaterialTheme {
                _contents.contents()
                contents.initCompose(contextInitializer(ctx))
                additionalContents.forEach {
                    it()
                }
            }
        }
    }
}

