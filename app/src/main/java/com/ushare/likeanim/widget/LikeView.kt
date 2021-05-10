package com.ushare.likeanim.widget

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.measureTimeMillis

class LikeView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defS: Int = 0) :
    View(ctx, attrs, defS) {

    companion object {
        private const val SYNC_MSG_WHAT = 100
        private const val ANIM_END_MSG_WHAT = 101

        private const val SYNC_INTERVAL = 30L
    }

    private var mAnimListener: AnimEndListener? = null
    private val mElements = LinkedList<Element>()

    private var mConfig: Config = Config.getDefaultConfig(resources)

    private var mLinesCount = 5
    private var mMaxElementCount = 50

    private val mHandler = MHandler(this)

    private val mPool = ElementPool()

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        mElements.filter { it.isActive() }.forEach { ele ->
            ele.evaluate(30)
            ele.draw(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeMessages(SYNC_MSG_WHAT)

        mConfig.clearCache()
    }

    fun setConfig(config: Config) {
        mConfig = config
    }

    private var mAnimStart = false

    fun launch(x: Float, y: Float) {
        val time = measureTimeMillis {
            makeElements(x, y)
        }
        Log.e("----------", "time: $time")

//        mHandler.removeMessages(SYNC_MSG_WHAT)
//        mHandler.sendEmptyMessageDelayed(SYNC_MSG_WHAT, SYNC_INTERVAL)

        if (!mAnimStart) {
            mHandler.sendEmptyMessage(SYNC_MSG_WHAT)
            mAnimStart = true
        }
    }

    fun reset() {
        if (mElements.isNotEmpty()) {
            mHandler.removeMessages(SYNC_MSG_WHAT)
            mAnimStart = false
            mElements.clear()
            invalidate()
        }
    }

    fun setAnimEndListener(listener: AnimEndListener) {
        this.mAnimListener = listener
    }

    private fun animEnd() {
        reset()
        post {
            mAnimListener?.onAnimEnd()
        }
    }

    private fun hasAnimRunning(): Boolean {
        return mElements.any { it.isActive() }
    }

    private fun makeElements(x: Float, y: Float) {
        repeat(mLinesCount) {
            if (mElements.size > mMaxElementCount) {
                val e = mElements.removeFirst()
                mPool.release(e)
            }

            mElements.add(mPool.acquire().apply {
                init(x, y)
                config(mConfig)
            })
        }
    }

    class MTask(ref: LikeView) : TimerTask() {
        private val weakRef = WeakReference(ref)
        override fun run() {
            if (weakRef.get()?.hasAnimRunning() == true) {
                weakRef.get()?.postInvalidate()
            } else {
                weakRef.get()?.animEnd()
            }
        }
    }

    class MHandler(ref: LikeView) : Handler(Looper.getMainLooper()) {
        private val weakRef = WeakReference(ref)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SYNC_MSG_WHAT -> {
                    weakRef.get()?.invalidate()
                    if (weakRef.get()?.hasAnimRunning() == true) {
                        sendEmptyMessageDelayed(SYNC_MSG_WHAT, SYNC_INTERVAL)
                    } else {
                        sendEmptyMessage(ANIM_END_MSG_WHAT)
                    }
                }
                ANIM_END_MSG_WHAT -> {
                    weakRef.get()?.animEnd()
                }
            }
        }
    }

    interface AnimEndListener {
        fun onAnimEnd()
    }
}