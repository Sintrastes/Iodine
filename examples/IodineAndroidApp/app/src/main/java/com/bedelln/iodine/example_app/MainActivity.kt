package com.bedelln.iodine.example_app

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.bedelln.iodine.android.ActivityCtx
import com.bedelln.iodine.android.IodineActivity
import com.bedelln.iodine.android.tools.AndroidAlertDialog
import com.bedelln.iodine.android.tools.Toast
import com.bedelln.iodine.components.*
import com.bedelln.iodine.imap
import com.bedelln.iodine.interfaces.Displayable
import com.bedelln.iodine.thenTool

class MainActivity: IodineActivity<ActivityCtx, Click, Void, Unit, Unit>(Unit) {
    override val contextInitializer = { it: ActivityCtx -> it }

    override val contents = ActionButton(
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
    )
}

data class TextItem(val contents: String): Displayable<ActivityCtx> {
    @Composable
    override fun ActivityCtx.display() {
        Text(contents)
    }
}

val String.TextItem get() = TextItem(this)