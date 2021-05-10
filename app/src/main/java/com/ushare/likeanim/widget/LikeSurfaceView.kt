package com.ushare.likeanim.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.measureTimeMillis

class LikeSurfaceView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defS: Int = 0) : SurfaceView(ctx, attrs, defS), SurfaceHolder.Callback {

    private val mHolder = holder
    private val mDrawThread = DrawThread(this, mHolder)

    private var mConfig: Config = Config.getDefaultConfig(resources)

    private val mElements = LinkedList<Element>()
    private var mLinesCount = 5
    private var mMaxElementCount = 50
    private val mPool = ElementPool()

    init {
        mHolder.addCallback(this)
        mHolder.setFormat(PixelFormat.TRANSLUCENT)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val canvas = holder.lockCanvas()
        if(canvas != null) {
            canvas.drawColor(Color.WHITE)
            holder.unlockCanvasAndPost(canvas);
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onDetachedFromWindow() {
        mDrawThread.isRun = false
        super.onDetachedFromWindow()
    }

    fun launch(x: Float, y: Float) {
        val time = measureTimeMillis {
            makeElements(x, y)
        }
        if (!mDrawThread.isAlive) {
            mDrawThread.start()
        }
        Log.e("----------", "time: $time")
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

    class DrawThread(ref: LikeSurfaceView, private val mHolder: SurfaceHolder) : Thread() {
        private val weakRef = WeakReference(ref)

        var isRun = true

        override fun run() {
            var deltaTime = 0L
            var tickTime = System.currentTimeMillis()
            while (isRun) {
                var c: Canvas? = null
                try {
                    c = mHolder.lockCanvas()
                    c?.drawColor(Color.WHITE)
                    synchronized(mHolder) {
                        val els = weakRef.get()?.mElements
                        if (els?.isNullOrEmpty() == true) {
                            isRun = false
                        } else {
                            els.filter {
                                it.isActive()
                            }.forEach {
                                c?.run {
                                    it.evaluate(deltaTime)
                                    it.draw(this)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    c?.let {
                        mHolder.unlockCanvasAndPost(it)
                    }
                }
                deltaTime = System.currentTimeMillis() - tickTime
                if (deltaTime < 16) {
                    try {
                        sleep(16 - deltaTime)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                tickTime = System.currentTimeMillis()
            }
        }
    }
}