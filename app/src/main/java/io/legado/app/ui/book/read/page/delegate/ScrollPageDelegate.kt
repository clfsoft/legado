package io.legado.app.ui.book.read.page.delegate

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.VelocityTracker
import io.legado.app.ui.book.read.page.PageView
import kotlin.math.abs

class ScrollPageDelegate(pageView: PageView) : PageDelegate(pageView) {

    // 滑动追踪的时间
    private val velocityDuration = 1000
    //速度追踪器
    private val mVelocity: VelocityTracker = VelocityTracker.obtain()

    override fun onAnimStart() {
        //惯性滚动
        fling(
            0, touchY.toInt(), 0, mVelocity.yVelocity.toInt(),
            0, 0, -10 * viewHeight, 10 * viewHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        curPage.onScroll(lastY - touchY)
    }

    override fun onAnimStop() {
        if (!isCancel) {
            pageView.fillPage(mDirection)
        }
    }

    override fun onTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            setStartPoint(event.x, event.y)
            abort()
            mVelocity.clear()
            mVelocity.addMovement(event)
        }
        return super.onTouch(event)
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        mVelocity.addMovement(e2)
        mVelocity.computeCurrentVelocity(velocityDuration)

        if (!isMoved && abs(distanceX) < abs(distanceY)) {
            if (distanceY < 0) {
                if (atTop) {
                    //如果上一页不存在
                    if (!hasPrev()) {
                        noNext = true
                        return true
                    }
                    setDirection(Direction.PREV)
                }
            } else {
                if (atBottom) {
                    //如果不存在表示没有下一页了
                    if (!hasNext()) {
                        noNext = true
                        return true
                    }
                    setDirection(Direction.NEXT)
                }
            }
            isMoved = true
        }
        if (isMoved) {
            isRunning = true
            //设置触摸点
            setTouchPoint(e2.x, e2.y)
        }
        return isMoved
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        mVelocity.addMovement(e2)
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onDestroy() {
        super.onDestroy()
        mVelocity.recycle()
    }
}