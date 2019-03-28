package com.annis.myviewpager

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Scroller
import android.widget.Toast

/**
 * @author Lee
 * @date 2019/3/28 11:12
 * @Description
 * function
 * 1.滑动     切换(控制可超出30%宽度)
 * 2.点击左右 切换(达到边界是 超出10%宽度 的动画)
 *
 */
class MyViewPager : ViewGroup {
    private val mDragDistance = 10

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    private var maxWidth: Int = 0
    private var spillScrollWidth: Int = 0//允许滑动溢出范围
    private var spillShowWidth: Int = 0//允许提示溢出范围
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        maxWidth = (childCount - 1) * width
        spillScrollWidth = (width * 0.3).toInt()
        spillShowWidth = (width * 0.1).toInt()
        for (i in 0 until childCount) {
            getChildAt(i).layout(i * width, 0, (i + 1) * width, height)
        }
    }

    private lateinit var scroller: Scroller
    private lateinit var gestureDetector: GestureDetector
    fun initView(context: Context) {
        scroller = Scroller(context)
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                var toX = scrollX + distanceX
                var dX = distanceX
                if (toX < -spillScrollWidth) {
                    dX = 0f
                }
                if (toX > maxWidth + spillScrollWidth) {
                    dX = 0f
                }
                Log.w("MyViewpager", "toX:$toX  (maxWidth + spillScrollWidth):${maxWidth + spillScrollWidth}")
                scrollBy(dX.toInt(), 0)
                return true
            }
        })
    }

    private var currentIndex = 0

    private var startX: Float = 0f
    private var isDrag = false//是否是拖拽事件
    private var showOut = false//是否显示视图外内容
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.w("MyViewpager", "onTouchEvent")
        var handle = gestureDetector.onTouchEvent(event)//false//默认传递给下级视图
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                //状态重置
                showOut = false
                isDrag = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(startX - event.x) > mDragDistance) {
                    isDrag = true
                    Log.w("MyViewpager", "isDrag")
                }
                handle = true
            }
            MotionEvent.ACTION_UP -> {
                //1.计算出将到达的页面
                if (!isDrag) { //点击事件  rawX
                    val change = event.rawX > (width / 2)
                    if (change) {
                        if (currentIndex == childCount - 1) {
                            showOut = true
                        }
                        currentIndex++
                    } else {
                        if (currentIndex == 0) {
                            showOut = true
                        }
                        currentIndex--
                    }
                } else {  //处理拖拽事件  x
                    if (event.x - startX > (width / 2)) {
                        currentIndex--
                    }
                    if (startX - event.x > (width / 2)) {
                        currentIndex++
                    }
                }
                //2.防止 角标 溢出
                if (currentIndex < 0) currentIndex = 0
                if (currentIndex >= childCount) currentIndex = childCount - 1
                //3.总距离计算出来
                val distanceX = currentIndex * width - scrollX
                //4.执行滚动
                if (!showOut) {
                    scroller.startScroll(scrollX, scrollY, distanceX, 0, Math.abs(distanceX))
                    invalidate()//强制绘制;//onDraw();computeScroll();
                } else {
                    pushOut()
                }
                //状态重置
                startX = 0f
            }
        }
        return true
    }

    override fun computeScroll() {
        Log.w("MyViewpager", "computeScroll")
        if (scroller.computeScrollOffset()) {//滑动中
            //得到移动这个一小段对应的坐标
            val currX = scroller.currX
            scrollTo(currX, 0)
            invalidate()
        } else {//不滑动了
            //是否需要归位
            if (showOut) {
                pullOut()
            }
        }
    }

    /**
     * 弹出边界
     */
    private fun pushOut() {
        scroller.startScroll(
            scrollX, scrollY, when (currentIndex) {
                0 -> -spillShowWidth
                else -> spillShowWidth
            }, 0, spillShowWidth * 2
        )
        invalidate()//强制绘制;//onDraw();computeScroll();
    }

    /**
     *  收回边界
     */
    private fun pullOut() {
        if (scrollX <= 0) {
            scroller.startScroll(scrollX, scrollY, -scrollX, 0, spillShowWidth * 2)
        } else if (scrollX >= maxWidth) {
            scroller.startScroll(scrollX, scrollY, maxWidth - scrollX, 0, spillShowWidth * 2)
        }
        invalidate()//强制绘制;//onDraw();computeScroll();
    }
}