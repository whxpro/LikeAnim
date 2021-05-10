package com.ushare.likeanim.widget

import android.graphics.Canvas

interface Element {
    fun init(x: Float, y: Float)

    fun config(config: Config?)

    fun evaluate(time: Long)

    fun draw(canvas: Canvas)

    fun isActive(): Boolean

    fun reset()
}