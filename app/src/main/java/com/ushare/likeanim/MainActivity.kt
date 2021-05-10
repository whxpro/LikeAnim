package com.ushare.likeanim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ushare.likeanim.widget.Config
import com.ushare.likeanim.widget.LikeSurfaceView
import com.ushare.likeanim.widget.LikeView

class MainActivity : AppCompatActivity() {
    private var likeView: LikeSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        likeView = findViewById(R.id.like_view)
        /*likeView?.setConfig(Config.getDefaultConfig(resources).setTotalTime(800))
        likeView?.setAnimEndListener(object : LikeView.AnimEndListener {
            override fun onAnimEnd() {
                Toast.makeText(this@MainActivity, "anim end", Toast.LENGTH_SHORT).show()
            }
        })*/

        findViewById<Button>(R.id.like_btn).setOnClickListener { v ->
            val x = v.x + v.width / 2
            val y = v.y + v.height / 2
            likeView?.launch(x, y)
        }

        findViewById<Button>(R.id.reset).setOnClickListener {
//            likeView?.reset()
        }
        findViewById<Button>(R.id.to_list).setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }
    }
}