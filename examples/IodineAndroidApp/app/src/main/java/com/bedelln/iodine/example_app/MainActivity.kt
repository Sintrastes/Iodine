package com.bedelln.iodine.example_app

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bedelln.iodine.WrappedComponent
import com.bedelln.iodine.android.ActivityCtx
import com.bedelln.iodine.android.IodineActivity
import com.bedelln.iodine.android.tools.AndroidAlertDialog
import com.bedelln.iodine.android.tools.Toast
import com.bedelln.iodine.components.*
import com.bedelln.iodine.android.components.*
import com.bedelln.iodine.imap
import com.bedelln.iodine.interfaces.Displayable
import com.bedelln.iodine.thenTool

class MainActivity: IodineActivity<ActivityCtx, DropdownMenu.Event<TextItem>, DropdownMenu.Event<TextItem>, TextItem, TextItem>("one".TextItem) {
    override val contextInitializer = { it: ActivityCtx -> it }

    override val contents = WrappedComponent(
        layout = {
            Column(
                modifier = Modifier.fillMaxSize(1.0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                it()
            }
        },
        component = DropdownMenu(
            dropdownItems = listOf(
                "one".TextItem,
                "two".TextItem
            )
        )
        /* ActionButton(
            text = "Hello, Android!",
            action = AndroidAlertDialog(
                title = "Hello!",
                contents = RadioGroup(
                    listOf(
                        "one".TextItem,
                        "two".TextItem
                    )
                )
                    .imap({ null }, { })
            ).thenTool { selected ->
                Toast(selected!!.contents)
            }
        ) */
    )
}

data class TextItem(val contents: String): Displayable<ActivityCtx> {
    @Composable
    override fun ActivityCtx.display() {
        Text(contents)
    }
}

val String.TextItem get() = TextItem(this)