package com.bedelln.iodine.android

import android.content.Context

/**
 * Iodine context for a component living inside an Android fragment.
 */
interface FragmentCtx: AndroidCtx {
    val fragmentCtx: Context
}