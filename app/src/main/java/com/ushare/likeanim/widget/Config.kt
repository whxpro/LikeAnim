package com.ushare.likeanim.widget

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.SparseArray
import androidx.annotation.DrawableRes
import com.ushare.likeanim.R
import com.ushare.likeanim.dp2px
import kotlin.random.Random

class Config(private val resources: Resources) {
    private var mMaxAngle = 360
    private var mMinAngle = 180
    private var mTotalTime = 1000L

    private val mIcons = arrayListOf<Int>()

    private val mBitmaps = SparseArray<Bitmap>()

    companion object {
        fun getDefaultConfig(resources: Resources): Config {
            return Config(resources).apply {
                setImages(
                    R.mipmap.icon_1,
                    R.mipmap.icon_2,
                    R.mipmap.icon_3,
                    R.mipmap.icon_4,
                    R.mipmap.icon_5,
                    R.mipmap.icon_6)
                setMaxAngle(315)
                setMinAngle(225)
                setTotalTime(1000)
            }
        }
    }

    fun setMaxAngle(maxAngle: Int): Config {
        mMaxAngle = maxAngle
        return this
    }

    fun setMinAngle(minAngle: Int): Config {
        mMinAngle = minAngle
        return this
    }

    fun setImages(@DrawableRes vararg resIds: Int): Config {
        if (resIds.isNotEmpty()) {
            mIcons.addAll(resIds.toList())
        }
        return this
    }

    fun setTotalTime(time: Long): Config {
        mTotalTime = time
        return this
    }

    fun getRandomBitmap(): Bitmap {
        val idx = (Random.nextInt(2 * mIcons.size + 1)) % mIcons.size
        var bitmap = mBitmaps[idx]
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(resources, mIcons[idx])
            mBitmaps.put(idx, bitmap)
        }
        return bitmap
    }

    fun getRandomAngle(): Int {
        return Random.nextInt(mMaxAngle - mMinAngle) + mMinAngle
    }

    fun getGravity(): Int {
        return dp2px(resources, 760.0)
    }

    fun getSpeed(): Int {
        return dp2px(resources, (550 + Random.nextInt(50)).toDouble())
    }

    fun getTotalTime(): Long {
        return mTotalTime
    }

    fun clearCache() {
        mBitmaps.clear()
    }
}