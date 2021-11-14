package com.bedelln.iodine.components.text

import com.bedelln.iodine.forms.ValidatedForm
import com.bedelln.iodine.forms.ValidatingForm
import com.bedelln.iodine.forms.ValidatingFormDescription
import com.bedelln.iodine.forms.ValidationEvent
import com.bedelln.iodine.interfaces.FormDescription
import com.bedelln.iodine.interfaces.IodineContext

object InvalidDouble

class DoubleEntry<C : IodineContext> : FormDescription<C, Any, ValidationEvent<Void>, String, Double?> by (
    ValidatedForm(
        object: ValidatingFormDescription<C, Unit, Void, String, Double, InvalidDouble> {
            override fun initialize(
                ctx: C,
                initialValue: String
            ): ValidatingForm<C, Unit, Void, String, Double, InvalidDouble> {
                TODO("Not yet implemented")
            }
        }
    )
)