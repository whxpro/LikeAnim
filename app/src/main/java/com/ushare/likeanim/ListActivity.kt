package com.ushare.likeanim

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ushare.likeanim.widget.Config
import com.ushare.likeanim.widget.LikeView

class ListActivity : AppCompatActivity() {

    private val lastClickTimeMap = HashMap<Int, Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_list)

        val likeView = findViewById<LikeView>(R.id.like_view)

        var like: Boolean
        findViewById<RecyclerView>(R.id.list).adapter = MAdapter().apply {
            setItemClickListener { v, pos ->
                like = !v.isSelected
                val lastTime = lastClickTimeMap[pos]
                val curTime = System.currentTimeMillis()
                if (lastTime == null || curTime - lastTime > 1000) {
                    v.isSelected = like
                }
                lastClickTimeMap[pos] = curTime

                if (like) {
                    val itemPosition = IntArray(2)
                    val likePosition = IntArray(2)
                    v.getLocationOnScreen(itemPosition)
                    likeView.getLocationOnScreen(likePosition)
                    val x = itemPosition[0] + v.width / 2
                    val y = itemPosition[1] - likePosition[1] + v.height / 2
                    likeView.launch(x.toFloat(), y.toFloat())
                }
            }
        }
    }

    class MAdapter : RecyclerView.Adapter<MHolder>() {

        private var mItemClickListener: ((View, Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MHolder {
            return MHolder(parent)
        }

        override fun onBindViewHolder(holder: MHolder, position: Int) {
            holder.likeBtn?.setOnClickListener {
                mItemClickListener?.invoke(it, position)
            }
        }

        override fun getItemCount(): Int {
            return 10
        }

        fun setItemClickListener(listener: (View, Int) -> Unit) {
            mItemClickListener = listener
        }
    }

    class MHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_list_item,
            parent,
            false
        )
    ) {
        val likeBtn: ImageView? = itemView.findViewById(R.id.item_like)

        fun bind() {

        }
    }
}