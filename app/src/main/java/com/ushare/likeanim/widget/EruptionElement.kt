package com.ushare.likeanim.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.animation.DecelerateInterpolator
import com.ushare.likeanim.dp2px
import kotlin.math.cos
import kotlin.math.sin

class EruptionElement : Element {

    private var mTotalTime = 1000L
    private var mAngle: Double = 270.0
    private var mSpeed: Double = 100.0
    private var mBitmap: Bitmap? = null

    private var mGravity = 1800

    private val mPaint by lazy {
        Paint()
    }
    private val mMatrix by lazy { Matrix() }

    private var mX = 0f
    private var mY = 0f

    private var mInitX = 0f
    private var mInitY = 0f

    private var mActive = false
    private var mCurTime = 0L

    private var mHalfImageX = 0f
    private var mHalfImageY = 0f

    private var mSpeedX = 0.0
    private var mSpeedY = 0.0

    private var mScale = 1f

    override fun init(x: Float, y: Float) {
        mInitX = x
        mInitY = y

    }

    override fun config(config: Config?) {
        config ?: return
        mActive = true
        mAngle = config.getRandomAngle().toDouble()
        mSpeed = config.getSpeed().toDouble()
        mBitmap = config.getRandomBitmap()
        mTotalTime = config.getTotalTime()
        mGravity = config.getGravity()

        mSpeedX = mSpeed * cos(Math.toRadians(mAngle))
        mSpeedY = mSpeed * sin(Math.toRadians(mAngle))

        mHalfImageX = (mBitmap?.width ?: 0).toFloat() / 2
        mHalfImageY = (mBitmap?.height ?: 0).toFloat() / 2
    }

    override fun evaluate(time: Long) {
        mBitmap ?: return

        mCurTime += time
        if (mCurTime >= mTotalTime) {
            mActive = false
            return
        }
        val timeS = mCurTime.toDouble() / mTotalTime
        val alpha = if (timeS < 0.65) 255 else (255 * (1 - timeS)).toInt()
        mPaint.alpha = alpha

        mScale = if (timeS > 0.18) {
            1f
        } else {
            0.45f.coerceAtLeast((timeS / 0.18).toFloat())
        }
        if (mScale < 0.5) {
            mPaint.alpha = 100
        }

        mX = (mInitX + mSpeedX * timeS - mBitmap!!.width / 2).toFloat()
        mY = (mInitY + mSpeedY * timeS + mGravity * timeS * timeS / 2 - mBitmap!!.height / 2).toFloat()
    }

    override fun draw(canvas: Canvas) {
        mBitmap ?: return

        mMatrix.reset()
        mMatrix.postScale(mScale, mScale, mHalfImageX, mHalfImageY)
        mMatrix.postTranslate(mX, mY)

        canvas.drawBitmap(mBitmap!!, mMatrix, mPaint)
    }

    override fun isActive() = mActive

    override fun reset() {
        mBitmap = null
        mCurTime = 0
        mActive = false
        mPaint.reset()
        mMatrix.reset()
    }
}