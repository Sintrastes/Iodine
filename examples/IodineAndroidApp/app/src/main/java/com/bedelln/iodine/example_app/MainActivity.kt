package com.bedelln.iodine.example_app

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.Dp
import com.bedelln.iodine.android.ActivityCtx
import com.bedelln.iodine.android.IodineActivity
import com.bedelln.iodine.android.tools.AndroidAlertDialog
import com.bedelln.iodine.components.*
import com.bedelln.iodine.components.text.IntEntry
import com.bedelln.iodine.interfaces.*

class MainActivity: IodineActivity<ActivityCtx, Void, Void, Unit, Unit>(Unit) {
    override val contextInitializer = { it: ActivityCtx -> it }

    override val contents =
        Column<ActivityCtx>(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(
                    horizontal = Dp(5.0f),
                    vertical   = Dp(5.0f)
                ))
        ) {
            val intEntry = IntEntry<ActivityCtx>()
                .initialValue("1")
                .not().bind()
            ActionButton(
                text = "Submit",
                action = Tool.create<ActivityCtx, Unit, Unit> {
                    intEntry.onEvent(
                        ValidationEvent.SubmitForValidation()
                    )
                }
            ).not().bind()

            IodineMonad
                .Return(Unit)
        }
}

data class TextItem(val contents: String): Displayable<ActivityCtx> {
    @Composable
    override fun ActivityCtx.display() {
        Text(contents)
    }
}

val test: ToolDescription<ActivityCtx,Unit,TextItem?> = AndroidAlertDialog(
    title = "Hello!",
    contents = RadioGroup(
        listOf(
            "one".TextItem,
            "two".TextItem
        )
    )
        .initialValue(null)
)

val String.TextItem get() = TextItem(this)
/*
val TestTool = Tool {
    val selected: TextItem? = test.bind()
    Toast(
        selected!!.contents
    )
}
 */