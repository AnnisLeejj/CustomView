package com.annis.myviewpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val imgs = arrayListOf(
        R.mipmap.a1, R.mipmap.a2, R.mipmap.a3, R.mipmap.a4, R.mipmap.a5, R.mipmap.a6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        //方式一：这句代码必须写在setContentView()方法的后面
//        supportActionBar?.hide()
        //方式二：这句代码必须写在setContentView()方法的前面
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgs.forEach {
            var imageView = ImageView(this@MainActivity)
            imageView.setBackgroundResource(it)

            viewPager.addView(imageView)
        }
    }
}
