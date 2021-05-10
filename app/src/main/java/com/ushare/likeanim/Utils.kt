package com.ushare.likeanim

import android.content.Context
import android.content.res.Resources

fun dp2px(resources: Resources, dip: Double): Int {
    if (resources.displayMetrics != null) {
        val density = resources.displayMetrics.density.toDouble()
        return (density * dip + 0.5).toInt()
    }
    return 0
}

fun getScreenWidth(resources: Resources): Int {
    return resources.displayMetrics?.widthPixels ?: 0
}