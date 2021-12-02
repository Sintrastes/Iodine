package com.bedelln.iodine.interfaces

interface HasImpl<out I> {
    val impl: I
}