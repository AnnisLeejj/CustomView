package com.annis.attributeview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap

/**
 * @author Lee
 * @date 2019/3/27 10:24
 * @Description
 */
const val nameSpace = "http://schemas.android.com/apk/res-auto"

class MyAttributeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    val TAG = this.javaClass.simpleName
    lateinit var mName: String
    var mAge: Int = 0
    var mHeader: Int = 0
    var headerDrawable: Drawable? = null

    private var paint: Paint = Paint()

    init {
        //获取属性三种方式
        //1.使用命名空间获取
        val name = attrs?.getAttributeValue(nameSpace, "my_name")
        Log.w(TAG, "nameSpace:  $name")

        attrs?.apply {
            //2.遍历属性集合
            for (i in 0 until attrs.attributeCount) {
                Log.w(TAG, "iterator:  ${attrs.getAttributeName(i)}-->${attrs.getAttributeValue(i)}")
            }
            //3.使用系统工具,获取集合
            var typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyAttributeView)
            for (i in 0 until typedArray.indexCount) {
                val index = typedArray.getIndex(i)
                when (index) {
                    R.styleable.MyAttributeView_my_name -> {
                        mName = typedArray.getString(index)
                    }
                    R.styleable.MyAttributeView_my_age -> {
                        mAge = typedArray.getInt(index, 0)
                    }
                    R.styleable.MyAttributeView_my_header -> {
                        //mHeader = typedArray.getInt(index, R.drawable.abc_ab_share_pack_mtrl_alpha)
                        headerDrawable = typedArray.getDrawable(index)
                    }
                }
            }
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        paint.color = Color.BLACK
        paint.textSize = 26f
        canvas?.drawText("$mName - $mAge", 50f, 50f, paint)
//        canvas.drawText("$mName - $mAge", 50f, 50f, paint)
        headerDrawable?.also {
            //            it.draw(canvas!!)//这个方法会清空canvas
            canvas?.drawBitmap(it.toBitmap(), 0f, 50f, paint)
        }
    }
}
