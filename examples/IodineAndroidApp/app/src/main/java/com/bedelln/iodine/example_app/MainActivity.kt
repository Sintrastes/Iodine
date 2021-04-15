package com.bedelln.iodine.example_app

import com.bedelln.iodine.android.ActivityCtx
import com.bedelln.iodine.android.IodineActivity
import com.bedelln.iodine.android.tools.AndroidAlertDialog
import com.bedelln.iodine.components.*
import com.bedelln.iodine.imap
import com.bedelln.iodine.omap

class MainActivity: IodineActivity<ActivityCtx, Void, Unit, Unit>(Unit) {
    override val contextInitializer = { it: ActivityCtx -> it }

    override val contents = ActionButton(
        text = "Hello, Android!",
        action = AndroidAlertDialog(
            title = "Hello!",
            contents = TextEntry<ActivityCtx>()
                .imap { it: Unit -> "" }
                .omap { }
        )
    )
}